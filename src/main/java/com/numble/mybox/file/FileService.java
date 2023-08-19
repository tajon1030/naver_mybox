package com.numble.mybox.file;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.repository.FolderPathRepository;
import com.numble.mybox.user.entity.User;
import com.numble.mybox.user.repository.UserRepository;
import com.numble.mybox.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.numble.mybox.util.ObjectStorage.putObject;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FolderPathRepository folderPathRepository;
    private final UserRepository userRepository;

    public void upload(MultipartFile multipartFile, Long userId, Long folderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getUnusedQuota() < multipartFile.getSize()) {
            throw new CustomException(ErrorCode.DISK_OUT_OF_SPACE);
        }

        // 회원의 총 사용 용량 추가
        userRepository.saveAndFlush(user.useQuota(multipartFile.getSize()));

        // 파일 정보 저장
        String originalFilename = multipartFile.getOriginalFilename();

        String path = folderPathRepository.findByDescendantAndUserId(folderId, userId)
                .stream().map(Folder::getName)
                .collect(Collectors.joining("/", "", "/"));

        File file = File.builder()
                .oriName(originalFilename)
                .saveName(originalFilename + UUID.randomUUID())
                .fileType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .uploadPath(path)
                .userId(user.getId())
                .folderId(folderId)
                .build();
        fileRepository.save(file);

        try {
            putObject(path + originalFilename, Utils.multipartToFile(multipartFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(Long fileId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
        if (!Objects.equals(file.getUserId(), userId)) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        // 회원의 총 사용 용량 원복
        userRepository.saveAndFlush(user.returnQuota(file.getSize()));
        // 파일 정보 삭제
        fileRepository.delete(file);
        // TODO ObjectStorage 파일 제거
    }

    @Transactional(readOnly = true)
    public List<File> getFileList(Long folderId, Long userId) {
        return fileRepository.findByFolderIdAndUserId(folderId, userId);
    }

    public void deleteFileWithFolderId(Long folderId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<File> fileList = fileRepository.findByFolderIdAndUserId(folderId, userId);
        // 회원의 총 사용 용량 원복
        long totalSize = fileList.stream().mapToLong(File::getSize).sum();
        userRepository.saveAndFlush(user.returnQuota(totalSize));
        // 파일 정보 삭제
        fileRepository.deleteAll(fileList);

        // TODO ObjectStorage 파일 제거
    }
}
