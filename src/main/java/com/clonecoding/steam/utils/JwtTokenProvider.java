package com.clonecoding.steam.utils;


import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expire-time}")
    private Long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token-expire-time}")
    private Long REFRESH_TOKEN_EXPIRE_TIME;


    /**
     * methodName : sign
     * Author : Minseok Kim
     * description : 엑세스 토큰을 생성하는 메서드
     *
     * @param : NtUser user - 토큰을 생성하려는 유저 정보
     * @param : now : token을 생성하는 시간
     * @return : 엑세스 토큰 리턴
     */
    public String sign(User user, Date now) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("uid", user.getUid());

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
     * Author : minseok Kim
     * description : 엑세스 토큰의 값을 읽고 결과값을 반환하는 함수
     *
     * @param : String Token - 해독하려는 토큰
     * @return : 해독하려는 토큰의 해독 결과
     */
    public TokenVerificationResult verify(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return TokenVerificationResult.builder()
                    .ok(true)
                    .uid((Integer) claims.getBody().get("uid"))
                    .userID(claims.getBody().getSubject())
                    .category((String) claims.getBody().get("Category"))
                    .build();

            // 토큰이 만료된 경우와, 순수 JWT 토큰 오류를 구별해 Exception을 Throw한다.
        }catch (ExpiredJwtException e){
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(ExceptionMessages.INVALID_TOKEN.getMessage(), e);
        }
    }

    /**
     * methodName : refresh
     * Author : Minseok Kim
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


    @Getter
    @Builder
    public static class TokenVerificationResult {
        private boolean ok;
        private Integer uid;
        private String userID;
        private String category;
        private String message;
    }
}