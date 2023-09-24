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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Date;

import static com.clonecoding.steam.utils.TokenOperationHelper.createRefreshTokenCookie;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException{

        User user = getUser(authentication);

        Date tokenCreationTime = new Date();
        String accessToken = jwtTokenProvider.sign(user, tokenCreationTime);
        String refreshToken = jwtTokenProvider.createRefresh(tokenCreationTime);
        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken, jwtTokenProvider.getREFRESH_TOKEN_EXPIRE_TIME());

        // Save refresh token to Redis
        redisService.setValuesWithTimeout(user.getUid(), refreshToken, jwtTokenProvider.getREFRESH_TOKEN_EXPIRE_TIME());

        log.info("사용자 Access 토큰 : {} ", accessToken);
        response.addCookie(refreshTokenCookie);
        getRedirectStrategy().sendRedirect(request, response, String.format("http://localhost:3000/callback?token=%s", accessToken));
    }

    private User getUser(Authentication authentication) {
        String email = ((OAuth2UserInfo) authentication.getPrincipal()).getEmail();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("email 값으로 유저를 찾을 수 없습니다"));
    }

}
