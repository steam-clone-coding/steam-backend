package com.clonecoding.steam.filter.common;


import com.clonecoding.steam.dto.response.ErrorResponse;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * methodName : doFilterInternal
     * Author : Minseok Kim
     * description : 로그인 실패시 나타나는 UnAuthorizedExcepton을 catch해 사용자에게 401 코드와 오류 메시지를 전달하도록 하는 메서드
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch(UnAuthorizedException | JwtException e){
            String errorResBody = objectMapper.writeValueAsString(new ErrorResponse(e));

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
            response.getWriter().write(errorResBody);
        }
    }
}
