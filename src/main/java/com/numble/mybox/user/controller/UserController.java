package com.numble.mybox.user.controller;

import com.numble.mybox.user.dto.EmailPasswordDto;
import com.numble.mybox.user.dto.UserResponse;
import com.numble.mybox.user.entity.User;
import com.numble.mybox.user.mapper.UserMapper;
import com.numble.mybox.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * 회원가입
     *
     * @param emailPassword
     * @return
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> signUp(@RequestBody EmailPasswordDto emailPassword) {
        User savedUser = userService.signUp(userMapper.toUser(emailPassword));
        return ResponseEntity.ok(Map.of("email", savedUser.getEmail()));
    }

    /**
     * 로그인(세션방식)
     *
     * @param request
     * @param emailPassword
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(HttpServletRequest request, @RequestBody EmailPasswordDto emailPassword) {
        User loginUser = userService.login(userMapper.toUser(emailPassword));
        // 세션저장
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", loginUser);
        return ResponseEntity.ok().build();
    }

    /**
     * 로그아웃
     *
     * @param request
     * @return
     */
    @GetMapping("logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // 세션 삭제
        request.getSession(false).invalidate();
        return ResponseEntity.ok().build();
    }

    /**
     * 내 정보 조회
     *
     * @return
     */
    @GetMapping("/my")
    public ResponseEntity<UserResponse> myInfo(@SessionAttribute("loginUser") User user) {
        UserResponse myInfo = userMapper.toUserResponse(userService.getMyInfo(user.getEmail()));
        return ResponseEntity.ok(myInfo);
    }
}
