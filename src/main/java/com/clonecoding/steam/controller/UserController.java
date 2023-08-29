package com.clonecoding.steam.controller;

import com.clonecoding.steam.dto.request.UserRegisterRequest;
import com.clonecoding.steam.dto.response.ApiResponse;
import com.clonecoding.steam.entity.Dummy;
import com.clonecoding.steam.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody UserRegisterRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        try {
            // UserService를 통해 사용자 정보를 데이터베이스에 저장
            userService.registerUser(username, password);

            ApiResponse<String> respBody = ApiResponse.<String>builder()
                    .data("회원가입에 성공했습니다.")
                    .build();

            return new ResponseEntity<>(respBody, HttpStatus.OK);
        } catch (Exception e) {
            // 회원가입 실패시 예외 처리
            ApiResponse<String> respBody = ApiResponse.<String>builder()
                    .data("회원가입에 실패했습니다.")
                    .build();

            return new ResponseEntity<>(respBody, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody UserRegisterRequest req) {
        String username = req.getUsername();
        String password = req.getPassword();

        try {
            // UserService를 통해 로그인을 시도하고 Access Token을 반환
            String accessToken = userService.loginUser(username, password);

            ApiResponse<String> respBody = ApiResponse.<String>builder()
                    .data(accessToken)
                    .build();

            return new ResponseEntity<>(respBody, HttpStatus.OK);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            // 로그인 실패시 예외 처리
            ApiResponse<String> respBody = ApiResponse.<String>builder()
                    .data("로그인에 실패했습니다.")
                    .build();

            return new ResponseEntity<>(respBody, HttpStatus.UNAUTHORIZED);
        }
    }
}
