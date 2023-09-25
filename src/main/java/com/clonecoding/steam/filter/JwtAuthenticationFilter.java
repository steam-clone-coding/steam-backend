package com.clonecoding.steam.filter;

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
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 해당 메서드는 UsernamePasswordAuthentication을 수행하기 전에 호출되며,
     * JWT Token이 유효한지를 검증하고 필요한 처리를 수행하는 역할을 합니다.
     * 특히 이 필터에서는 POST api/refresh를 통한 토큰 재발급도 처리합니다.
     * @Author Jinyeong Seol
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 서블릿 필터 체인
     * @throws ServletException 필터 체인 처리 중 발생한 서블릿 예외
     * @throws IOException IO 예외
     * @throws JwtException JWT 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, JwtException {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwtToken = getAccessToken(request);
        JwtTokenProvider.TokenVerificationResult verificationResult;

        if (request.getRequestURI().equals("/api/refresh") && request.getMethod().equals("POST")) {
            // 토큰 재발급 요청일 경우에 대한 처리
            processRefreshRequest(request, response, jwtToken);
        } else {
            // 재발급 요청이 아닌 통상적인 인가 과정 수행
            try {
                // JWT Token의 유효성을 검증한다.
                verificationResult = jwtTokenProvider.verify(jwtToken);
            } catch (ExpiredJwtException e) {
                throw new JwtException(ExceptionMessages.EXPIRED_TOKEN.getMessage(), e);
            }

            // 예외가 발생하지 않으면 token 검증이 완료된 것이므로, SecurityContext에 유저 정보 저장
            final Authentication authentication = jwtTokenProvider.getAuthentication(verificationResult.getUid(), verificationResult.getUserRole());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 토큰 재발급 요청 시 해당 메서드가 호출되어 처리됩니다.
     * @Author Jinyeong Seol
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param jwtToken    현재의 JWT 토큰
     * @throws IOException IO 예외
     */
    private void processRefreshRequest(HttpServletRequest request, HttpServletResponse response, String jwtToken) throws IOException {
        // accessToken 재발급 요청인지 검증
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new JwtException(ExceptionMessages.NO_REFRESH_TOKEN.getMessage());
        }

        boolean refreshTokenFound = false; // refresh_token 쿠키 발견 여부 플래그
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh_token")) {
                try {
                    jwtTokenProvider.verify(jwtToken);
                } catch (ExpiredJwtException e) {
                    // verify에서 발생한 accessToken 만료 에러 처리
                    handleTokenRefresh(response, cookie.getValue(), e.getClaims());
                    return;
                }
                throw new JwtException(ExceptionMessages.REFRESH_WITH_NOT_EXPIRED_TOKEN.getMessage());
            }
        }
        if (!refreshTokenFound) {
            throw new JwtException(ExceptionMessages.NO_REFRESH_TOKEN.getMessage()); // 예외 처리 추가
        }
    }

    /**
     * 토큰을 갱신하여 새로운 access token을 발급합니다.
     * @Author Jinyeong Seol
     * @param response        HTTP 응답 객체
     * @param refreshToken   현재의 refresh token
     * @param claims          JWT 토큰의 claims
     * @throws IOException IO 예외
     */
    private void handleTokenRefresh(HttpServletResponse response, String refreshToken, Claims claims) throws IOException {
        jwtTokenProvider.verifyRefreshToken(refreshToken); // refreshToken 유효성 검증

        String storedRefreshToken = redisService.getValues(claims.get("uid").toString());

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            // refresh token이 token 저장소 내에 존재하지 않거나, 불일치한 경우
            throw new JwtException(ExceptionMessages.INVALID_REFRESH_TOKEN.getMessage());
        }

        // 기존 만료된 access_token에서 관련 정보를 취하고, 새 access token을 생성하는데 사용
        User user = User.builder().uid(claims.get("uid").toString()).userRole(UserAuthority.valueOf(claims.get("userRole").toString())).build();
        String newAccessToken = jwtTokenProvider.sign(user, new Date());

        LoginResponse resBody = LoginResponse.builder()
                .uid(user.getUid())
                .accessToken(newAccessToken)
                .build();

        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(resBody));
    }

    /**
     * HTTP 요청 헤더에서 Bearer 토큰을 추출하여 반환합니다.
     * @Author Jinyeong Seol
     * @param request  HTTP 요청 객체
     * @return 추출한 JWT 토큰
     */
    private String getAccessToken(HttpServletRequest request) {
        if (!request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ")) {
            throw new UnAuthorizedException(ExceptionMessages.INVALID_TOKEN.getMessage());
        }

        String[] header = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ");
        return header[1];
    }
}
