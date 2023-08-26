package com.clonecoding.steam.utils;


import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.service.RedisService;
import io.jsonwebtoken.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Getter
public class JwtTokenProvider {

    private String secretKey;


    private final Long ACCESS_TOKEN_EXPIRE_TIME;
    private final Long REFRESH_TOKEN_EXPIRE_TIME;

    private final RedisService redisService;

    public JwtTokenProvider(Environment env, RedisService redisService) {
        secretKey = env.getProperty("jwt.secret");
        ACCESS_TOKEN_EXPIRE_TIME = Long.parseLong(env.getProperty("jwt.access-token-expire-time"));
        REFRESH_TOKEN_EXPIRE_TIME = Long.parseLong(env.getProperty("jwt.refresh-token-expire-time"));
        this.redisService = redisService;
    }

    /**
     * methodName : sign
     * Author : Minseok Kim
     * description : 엑세스 토큰을 생성하는 메서드
     *
     * @param : User user - 토큰을 생성하려는 유저 정보
     * @param : now : token을 생성하는 시간
     * @return : 엑세스 토큰 리턴
     */
    public String sign(User user, Date now) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("uid", user.getUid());
        claims.put("userRole", user.getUserRole());

        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * methodName : verify
     * Author : Jinyeong Seol
     * description : 엑세스 토큰의 값을 읽고 결과값을 반환하는 함수
     *
     * @param : String Token - 해독하려는 토큰
     * @return : 해독하려는 토큰의 해독 결과
     * @exception ExpiredJwtException 토큰이 만료되었을 떄
     * @exception JwtException 토큰을 읽는 중 오류가 있을 때
     */
    public TokenVerificationResult verify(String token) throws ExpiredJwtException, JwtException{
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return TokenVerificationResult.builder()
                    .uid(claims.getBody().get("uid").toString())
                    .userID(claims.getBody().getSubject())
                    .userRole(UserAuthority.valueOf((String)claims.getBody().get("userRole")))
                    .build();

            // 토큰이 만료된 경우와, 순수 JWT 토큰 오류를 구별해 Exception을 Throw
        }catch (ExpiredJwtException e) {
            // 토큰 만료의 경우는 AuthenticationFilter 에서 상황에 따른 유연한 처리 진행
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 자체의 문제(유효하지 않거나 존재하지 않음)
            throw new JwtException(ExceptionMessages.INVALID_TOKEN.getMessage(), e);
        }
    }

    /**
     * methodName : verifyRefreshToken
     * Author : Jinyeong Seol
     * description : Refresh Token의 값을 읽고 결과값을 반환하는 메서드
     *
     * @param : String refreshToken - 해독하려는 Refresh Token
     * @exception ExpiredJwtException 토큰이 만료되었을 때
     * @exception JwtException 토큰을 읽는 중 오류가 있을 때
     */
    public void verifyRefreshToken(String refreshToken) throws ExpiredJwtException, JwtException {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new JwtException(ExceptionMessages.EXPIRED_REFRESH_TOKEN.getMessage(), e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Invalid refresh token", e);
        }
    }

    /**
     * methodName : refresh
     * Author : Jinyeong Seol
     * description : Refresh Token을 생성하는 메서드
     *
     * @param : Date now - Refresh Token을 생성할 시간
     * @return : Refresh Token
     */
    public String createRefresh(Date now) {
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims getClaim(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * @methodName getAuthentication
     * @author Minseok kim
     * @description token으로 부터 유저 정보를 추출해 Authentication 객체를 생성하는 메서드
     *
     * @param  uid Jwt Token에서 추출된 uid
     * @param userAuthority Jwt Token에서 추출된 사용자의 권한
     * @return Spring Security Context에 저장할 Authentication 객체
     */
    public Authentication getAuthentication(String uid,  UserAuthority userAuthority){

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(userAuthority.name());

        return new UsernamePasswordAuthenticationToken(uid, "",List.of(simpleGrantedAuthority));
    }

    @Getter
    @Builder
    public static class TokenVerificationResult {
        private String uid;
        private String userID;
        private UserAuthority userRole;
        private String message;
    }
}