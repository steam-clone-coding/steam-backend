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
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = ((OAuth2UserInfo) authentication.getPrincipal()).getEmail();

        User findUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("email 값으로 유저를 찾을 수 없습니다"));

        Date tokenCreationTime = new Date(new Date().getTime());

        String accessToken = jwtTokenProvider.sign(findUser, tokenCreationTime);
        String refreshToken = jwtTokenProvider.createRefresh(tokenCreationTime);

        // Save refresh token to Redis
        redisService.setValuesWithTimeout(findUser.getUid(), refreshToken, jwtTokenProvider.getREFRESH_TOKEN_EXPIRE_TIME());

        // refreshToken을 쿠키로 전송
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        // TODO: TOKEN_EXPIRE_TIME은 Long 이나 maxAge를 int 범위로 주어야 함.
        //  TOKEN_EXPIRE_TIME 이 만약 int범위를 넘어서면 ArithmeticException 이 발생함 (당장 문제 없으나 해결 필요)
        refreshTokenCookie.setMaxAge(Math.toIntExact(jwtTokenProvider.getREFRESH_TOKEN_EXPIRE_TIME())); // Set the cookie expiration time
        refreshTokenCookie.setPath("/"); // Set the cookie path

        response.addCookie(refreshTokenCookie);

        log.info("사용자 Access 토큰 : {} ", accessToken);

        getRedirectStrategy().sendRedirect(request, response, String.format("http://localhost:8080/token=%s", accessToken));

    }


}
