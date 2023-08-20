package com.numble.mybox.file;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.repository.FolderPathRepository;
import com.numble.mybox.folder.repository.FolderRepository;
import com.numble.mybox.user.entity.User;
import com.numble.mybox.user.repository.UserRepository;
import com.numble.mybox.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final FolderPathRepository folderPathRepository;
    private final UserRepository userRepository;
    private final S3Client s3Client;

    public void upload(MultipartFile multipartFile, Long userId, Long folderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 해당 폴더가 사용자의 폴더가 맞는지 확인
        folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(()->new CustomException(ErrorCode.INVALID_PERMISSION));

        // 같은 경로에 있는 파일과 이름이 중복되는지 확인
        validateDuplicationName(multipartFile.getOriginalFilename(), folderId);

        // 사용자 남은 용량 확인
        if (user.getUnusedQuota() < multipartFile.getSize()) {
            throw new CustomException(ErrorCode.DISK_OUT_OF_SPACE);
        }

        // 회원의 총 사용 용량 추가
        userRepository.saveAndFlush(user.useQuota(multipartFile.getSize()));

        // 파일 정보 저장
        String originalFilename = multipartFile.getOriginalFilename();
        String savedFilename = folderPathRepository.findByDescendantAndUserId(folderId, userId)
                .stream().map(Folder::getName)
                .collect(Collectors.joining("/", "", "/"))
                + originalFilename;
        MyFile myFile = MyFile.builder()
                .oriName(originalFilename)
                .fileType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .saveName(savedFilename)
                .userId(user.getId())
                .folderId(folderId)
                .build();
        fileRepository.save(myFile);

        // ObjectStorage 파일 저장
        try {
            s3Client.upload(multipartFile.getInputStream(), multipartFile.getSize(),
                    savedFilename, multipartFile.getContentType(),
                    Map.of("oriFileNm", originalFilename));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateDuplicationName(String name, Long folderId) {
        fileRepository.findFirstByFolderIdAndOriName(folderId, name)
                .ifPresent(myFile -> {throw new CustomException(ErrorCode.DUPLICATED_NAME);});
    }

    public void deleteFile(Long fileId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        MyFile myFile = fileRepository.findByUserIdAndId(userId, fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        // 회원의 총 사용 용량 원복
        userRepository.saveAndFlush(user.returnQuota(myFile.getSize()));
        // 파일 정보 삭제
        fileRepository.delete(myFile);
        // ObjectStorage 파일 제거
        s3Client.delete(myFile.getSaveName());
    }

    @Transactional(readOnly = true)
    public List<MyFile> getFileList(Long folderId, Long userId) {
        return fileRepository.findByFolderIdAndUserId(folderId, userId);
    }

    public void deleteFileWithFolderId(Long folderId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<MyFile> myFileList = fileRepository.findByFolderIdAndUserId(folderId, userId);
        if (!myFileList.isEmpty()) {
            // ObjectStorage 파일 제거
            s3Client.delete(myFileList.stream()
                    .map(file -> file.getSaveName())
                    .peek(System.out::println)
                    .toArray(String[]::new));
            // 회원의 총 사용 용량 원복
            long totalSize = myFileList.stream().mapToLong(MyFile::getSize).sum();
            userRepository.saveAndFlush(user.returnQuota(totalSize));
            // 파일 정보 삭제
            fileRepository.deleteAll(myFileList);
        }
    }

    @Transactional(readOnly = true)
    public MyFile downloadFile(Long fileId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        MyFile myFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
        if (!Objects.equals(myFile.getUserId(), userId)) {
            throw new CustomException(ErrorCode.INVALID_PERMISSION);
        }

        s3Client.download(myFile.getSaveName(), myFile.getOriName());
        return myFile;
    }
}
