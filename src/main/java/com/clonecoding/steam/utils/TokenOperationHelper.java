package com.clonecoding.steam.utils;

import jakarta.servlet.http.Cookie;


public class TokenOperationHelper {

    public static Cookie createRefreshTokenCookie(String refreshToken, Long expireTime) {
        // TODO: TOKEN_EXPIRE_TIME은 Long 이나 maxAge를 int 범위로 주어야 함.
        //  TOKEN_EXPIRE_TIME 이 만약 int범위를 넘어서면 ArithmeticException 이 발생함 (당장 문제 없으나 해결 필요)
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(Math.toIntExact(expireTime));
        refreshTokenCookie.setPath("/");
        return refreshTokenCookie;
    }
}
