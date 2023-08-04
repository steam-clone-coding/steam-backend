package com.clonecoding.steam.service;

import com.clonecoding.steam.auth.OAuth2UserInfo;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.repository.UserRepository;
import com.clonecoding.steam.utils.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Date;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String uid = ((OAuth2UserInfo) authentication.getPrincipal()).getUid();

        User findUser = userRepository.findUserByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("UID 값을 찾을 수 없습니다."));

        Date now = new Date();

        String accessToken = jwtTokenProvider.sign(findUser, now);
        String refreshToken = jwtTokenProvider.sign(findUser, now);

        response.addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
        response.addCookie(new Cookie("refresh-token", refreshToken));

        log.info("사용자 Access 토큰 : {} ", accessToken);

        getRedirectStrategy().sendRedirect(request, response, String.format("http://localhost:8080/token=%s", accessToken));

    }


}
