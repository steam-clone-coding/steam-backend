package com.clonecoding.steam.controller;

import com.clonecoding.steam.dto.UserRegisterDto;
import com.clonecoding.steam.dto.request.UserRegisterRequest;
import com.clonecoding.steam.dto.response.ApiResponse;
import com.clonecoding.steam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody UserRegisterDto req){

        userService.register(req);

        ApiResponse<String> respBody = ApiResponse.<String>builder()
                .data("회원가입에 성공했습니다.")
                .build();

        return new ResponseEntity<>(respBody, HttpStatus.OK);
    }
}