package com.clonecoding.steam.filter;

import com.clonecoding.steam.dto.request.LoginRequest;
import com.clonecoding.steam.dto.response.LoginResponse;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import com.clonecoding.steam.service.PrincipalUserDetailService;
import com.clonecoding.steam.service.RedisService;
import com.clonecoding.steam.utils.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;
import java.util.Date;

public class CustomUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisService redisService;


    public CustomUsernamePasswordAuthenticationFilter(RequestMatcher requestMatcher, AuthenticationManager authenticationManager) {
        super(requestMatcher, authenticationManager);
    }


    /**
     * methodName : attemptAuthentication
     * Author : Minseok Kim
     * description : 로그인 시도시 호출되는 메서드
     *
     * @param :
     * @return :
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        try {
            // username, password를 전달 받음.
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream().readAllBytes(), LoginRequest.class);

            // username, password를 기반으로 인증 토큰 발급
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            // UserDetailService.loadUser()를 호출해 로그인 정보가 올바른지 체크
            // com.knlab.gps.service.AuthPrincipalUserService 참고
            Authentication authenticate = getAuthenticationManager().authenticate(authenticationToken);

            return authenticate;

        } catch (Exception e) {
            throw new UnAuthorizedException(e.getMessage());
        }
    }


    /**
     * methodName : successfulAuthentication
     * Author : minseok Kim
     * description : 로그인이 성공할시 호출되는 메서드, Response에 유저정보, 토큰 정보를 설정해 리턴
     *
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalUserDetailService.CustomUserDetails userDetails =
                (PrincipalUserDetailService.CustomUserDetails) authResult.getPrincipal();

        final User user = userDetails.getUser();

        Date tokenCreationTime = new Date(new Date().getTime());

        String accessToken = jwtTokenProvider.sign(user, tokenCreationTime);
        String refreshToken = jwtTokenProvider.createRefresh(tokenCreationTime);

        // Save refresh token to Redis
        redisService.setValuesWithTimeout(user.getUid(), refreshToken, jwtTokenProvider.getREFRESH_TOKEN_EXPIRE_TIME());

        LoginResponse resBody = LoginResponse.builder()
                .uid(user.getUid())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(resBody));
    }
}