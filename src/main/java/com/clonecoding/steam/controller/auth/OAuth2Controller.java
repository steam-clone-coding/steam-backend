package com.clonecoding.steam.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OAuth2Controller {
    @GetMapping("/login/kakao")
    public void kakaoLogin(HttpServletResponse res) throws IOException {

        res.sendRedirect("/oauth2/authorization/kakao");
    }

    @GetMapping("/login/naver")
    public void naverLogin(HttpServletResponse res) throws IOException {

        res.sendRedirect("/oauth2/authorization/naver");
    }

    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse res) throws IOException {

        res.sendRedirect("/oauth2/authorization/google");
    }
}