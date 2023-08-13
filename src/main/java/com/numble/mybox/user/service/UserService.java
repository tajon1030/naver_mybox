package com.numble.mybox.user.service;

import com.numble.mybox.exception.CustomException;
import com.numble.mybox.exception.ErrorCode;
import com.numble.mybox.user.repository.UserRepository;
import com.numble.mybox.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User signUp(User user) {
        return userRepository.save(user);
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
    public User getMyInfo(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
