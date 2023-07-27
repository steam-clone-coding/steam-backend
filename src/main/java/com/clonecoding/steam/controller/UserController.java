package com.clonecoding.steam.controller;

import com.clonecoding.steam.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    @PostMapping("/new")
    public ResponseEntity<ApiResponse>
}
