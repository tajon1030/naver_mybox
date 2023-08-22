package com.numble.mybox.user.service;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.folder.entity.Folder;
import com.numble.mybox.folder.service.FolderService;
import com.numble.mybox.user.entity.User;
import com.numble.mybox.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FolderService folderService;

    public User signUp(User user) {
        // 중복 이메일 검증
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u-> {throw new CustomException(ErrorCode.DUPLICATED_LOGIN_ID);});
        User savedUser = userRepository.saveAndFlush(user);
        // 최상위 폴더 추가
        folderService.addFolder(new Folder(String.valueOf(savedUser.getId()), savedUser), null, user.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User login(User user) {
        return userRepository.findByEmail(user.getEmail())
                .map(loginUser -> {
                    // 비밀번호 일치 확인
                    if (loginUser.getPassword().equals(user.getPassword())) {
                        return loginUser;
                    } else {
                        throw new CustomException(ErrorCode.INVALID_PASSWORD);
                    }
                })
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User getMyInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
