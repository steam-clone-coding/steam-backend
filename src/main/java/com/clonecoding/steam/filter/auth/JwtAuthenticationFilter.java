package com.clonecoding.steam.filter.auth;

import com.clonecoding.steam.dto.response.LoginResponse;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.user.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import com.clonecoding.steam.service.common.RedisService;
import com.clonecoding.steam.utils.auth.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    private static final String REFRESH_ENDPOINT = "/api/v1/refresh";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, JwtException {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = getAccessToken(request);

        if (isRefreshRequest(request)) {
            processRefreshRequest(request, response, jwtToken);
        } else {
            processAuthorizationRequest(request, response, jwtToken, filterChain);
        }
    }

    private boolean isRefreshRequest(HttpServletRequest request) {
        return request.getRequestURI().equals(REFRESH_ENDPOINT) && request.getMethod().equals("POST");
    }

    private void processAuthorizationRequest(HttpServletRequest request, HttpServletResponse response, String jwtToken, FilterChain filterChain) throws IOException, ServletException {
        JwtTokenProvider.TokenVerificationResult verificationResult = verifyToken(jwtToken);
        setAuthenticationAndContinueFilterChain(verificationResult, request, response, filterChain);
    }

    private JwtTokenProvider.TokenVerificationResult verifyToken(String jwtToken) {
        try {
            return jwtTokenProvider.verify(jwtToken);
        } catch (ExpiredJwtException e) {
            throw new JwtException(ExceptionMessages.EXPIRED_TOKEN.getMessage(), e);
        }
    }

    private void setAuthenticationAndContinueFilterChain(JwtTokenProvider.TokenVerificationResult verificationResult, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        final Authentication authentication = jwtTokenProvider.getAuthentication(verificationResult.getUid(), verificationResult.getUserRole());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private void processRefreshRequest(HttpServletRequest request, HttpServletResponse response, String jwtToken) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new JwtException(ExceptionMessages.NO_REFRESH_TOKEN.getMessage());
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME)) {
                try {
                    jwtTokenProvider.verify(jwtToken);
                } catch (ExpiredJwtException e) {
                    handleTokenRefresh(response, cookie.getValue(), e.getClaims());
                    return;
                }
                throw new JwtException(ExceptionMessages.REFRESH_WITH_NOT_EXPIRED_TOKEN.getMessage());
            }
        }

        throw new JwtException(ExceptionMessages.NO_REFRESH_TOKEN.getMessage());
    }

    private void handleTokenRefresh(HttpServletResponse response, String refreshToken, Claims claims) throws IOException {
        jwtTokenProvider.verifyRefreshToken(refreshToken);

        String storedRefreshToken = redisService.getValues(claims.get("uid").toString());

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new JwtException(ExceptionMessages.INVALID_REFRESH_TOKEN.getMessage());
        }

        User user = User.builder().uid(claims.get("uid").toString()).userRole(UserAuthority.valueOf(claims.get("userRole").toString())).build();
        String newAccessToken = jwtTokenProvider.sign(user, new Date());

        LoginResponse resBody = LoginResponse.builder()
                .uid(user.getUid())
                .accessToken(newAccessToken)
                .build();

        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(resBody));
    }

    private String getAccessToken(HttpServletRequest request) {
        if (!request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ")) {
            throw new UnAuthorizedException(ExceptionMessages.INVALID_TOKEN.getMessage());
        }

        String[] header = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ");
        return header[1];
    }
}
