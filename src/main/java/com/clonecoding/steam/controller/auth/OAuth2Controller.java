package com.clonecoding.steam.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OAuth2Controller {

    @Operation(summary = "카카오 로그인 API")
    @GetMapping("/login/kakao")
    public void kakaoLogin(HttpServletResponse res) throws IOException {

        res.sendRedirect("/oauth2/authorization/kakao");
    }



    @Operation(summary = "네이버 로그인 API")
    @GetMapping("/login/naver")
    public void naverLogin(HttpServletResponse res) throws IOException {

        res.sendRedirect("/oauth2/authorization/naver");
    }



    @Operation(summary = "구글 로그인 API")
    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse res) throws IOException {

        res.sendRedirect("/oauth2/authorization/google");
    }
}