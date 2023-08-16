package com.numble.mybox.file;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.user.entity.User;
import com.numble.mybox.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public void upload(MultipartFile multipartFile, Long id, Long folderId) {
        User user = userRepository.findById(id)
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
                .saveName(originalFilename+ UUID.randomUUID())
                .fileType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .uploadPath("")
                .userId(user.getId())
                .folderId(folderId)
                .build();
        fileRepository.save(file);

        // ObjectStorage 업로드
    }
}
