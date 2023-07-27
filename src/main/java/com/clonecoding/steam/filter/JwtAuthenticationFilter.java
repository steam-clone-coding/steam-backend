package com.clonecoding.steam.filter;

import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import com.clonecoding.steam.utils.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    /**
     * @methodName doFilterInternal
     * @author Minseok kim
     * @description UsernamePasswordAuthentication을 수행하기 전에, JWT Token을 받아서 Token이 유효한지 체크하는 메서드. Token이 유효하다면 로그인을 수행하지 않고 패스한다.
     *
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Http Header로 부터 토큰을 받아온다.
        if(request.getHeader(HttpHeaders.AUTHORIZATION) == null){
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = getAccessToken(request);

        // JWT Token의 유효성을 검증한다.
        JwtTokenProvider.TokenVerificationResult verificationResult = tokenProvider.verify(jwtToken);

        // exception이 터지지 않으면 정상적으로 token 검증이 완료된 것이므로, SecurityContext에 유저 정보 저장
        final Authentication authentication = tokenProvider.getAuthentication(verificationResult.getUid(), verificationResult.getUserRole());
        SecurityContextHolder.getContext().setAuthentication(authentication);


    }

    private String getAccessToken(HttpServletRequest request) {
        if(!request.getHeader(HttpHeaders.AUTHORIZATION).startsWith("Bearer ")){
            throw new UnAuthorizedException(ExceptionMessages.INVALID_TOKEN.getMessage());
        }

        String[] header = request.getHeader(HttpHeaders.AUTHORIZATION).split(" ");
        return header[1];
    }
}
