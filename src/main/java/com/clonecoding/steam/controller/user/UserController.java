package com.clonecoding.steam.controller.user;

import com.clonecoding.steam.dto.request.UserRegisterDTO;
import com.clonecoding.steam.service.user.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;


    @Operation(summary = "사용자 회원가입 API")
    @PostMapping("/new")

    public ResponseEntity<Void> register(@RequestBody UserRegisterDTO req) {

        userService.register(req);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @Operation(summary = "사용자 로그인 API")
    @PostMapping("/login")
    public  ResponseEntity<Void> login() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}