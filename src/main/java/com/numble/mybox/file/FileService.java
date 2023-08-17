package com.numble.mybox.file;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.user.entity.User;
import com.numble.mybox.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
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
        File file = File.builder()
                .oriName(originalFilename)
                .saveName(originalFilename + UUID.randomUUID())
                .fileType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .uploadPath("")
                .userId(user.getId())
                .folderId(folderId)
                .build();
        fileRepository.save(file);

        // TODO ObjectStorage 업로드
    }

    public void deleteFile(Long fileId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
        if (!Objects.equals(file.getUserId(), userId)) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        // 회원의 총 사용 용량 추가
        userRepository.saveAndFlush(user.returnQuota(file.getSize()));
        // 파일 정보 삭제
        fileRepository.delete(file);
        // TODO ObjectStorage 파일 제거
    }

    @Transactional(readOnly = true)
    public List<File> getFileList(Long folderId, Long userId) {
        return fileRepository.findByFolderIdAndUserId(folderId, userId);
    }
}
