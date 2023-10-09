package com.clonecoding.steam.controller.user;

import com.clonecoding.steam.dto.request.UserRegisterDTO;
import com.clonecoding.steam.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/new")
    public ResponseEntity<Void> register(@RequestBody UserRegisterDTO req) {

        userService.register(req);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public  ResponseEntity<Void> login() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}