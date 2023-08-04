package com.clonecoding.steam.filter;

import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import com.clonecoding.steam.service.RedisService;
import com.clonecoding.steam.utils.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final RedisService redisService;

    /**
     * @methodName doFilterInternal
     * @author Minseok kim
     * @description UsernamePasswordAuthentication을 수행하기 전에, JWT Token을 받아서 Token이 유효한지 체크하는 메서드. Token이 유효하다면 로그인을 수행하지 않고 패스한다.
     *
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, JwtException {
        if(request.getHeader(HttpHeaders.AUTHORIZATION) == null){
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = getAccessToken(request);
        JwtTokenProvider.TokenVerificationResult verificationResult;

        try {
            // JWT Token의 유효성을 검증한다.
            verificationResult = tokenProvider.verify(jwtToken);
        } catch (ExpiredJwtException e) {
            // verify에서 발생한 accesToken 만료 Error을 받음
            if (request.getHeader("X-Refresh-Token") != null) {
                // RefreshToken이 Header에 존재하는 경우 아래 작업 수행
                handleTokenRefreshRequest(request, response, e.getClaims());
                return;
            } else {
                throw e;
            }
        }

        // exception이 터지지 않으면 정상적으로 token 검증이 완료된 것이므로, SecurityContext에 유저 정보 저장
        final Authentication authentication = tokenProvider.getAuthentication(verificationResult.getUid(), verificationResult.getUserRole());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void handleTokenRefreshRequest(HttpServletRequest request, HttpServletResponse response, Claims claims) throws IOException {
        try {
            String refreshToken = request.getHeader("X-Refresh-Token");

            tokenProvider.verifyRefreshToken(refreshToken); // refreshToken 유효성 검증

            String storedRefreshToken = redisService.getValues(claims.get( "uid" ).toString());

            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new UnAuthorizedException("Invalid refresh token");
            }

            User user = User.builder().uid(claims.get( "uid" ).toString()).userRole((UserAuthority) claims.get( "userRole" )).build();
            String newAccessToken = tokenProvider.sign(user, new Date());

            System.out.println("newAccessToken: " + newAccessToken);
//            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
//            response.getWriter().write(objectMapper.writeValueAsString(newAccessToken));
//            response.setStatus(HttpStatus.OK.value());

        } catch (JwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token refresh failed: " + e.getMessage());
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        if(!request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ")){
            throw new UnAuthorizedException(ExceptionMessages.INVALID_TOKEN.getMessage());
        }

        String[] header = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ");
        return header[1];
    }
}
