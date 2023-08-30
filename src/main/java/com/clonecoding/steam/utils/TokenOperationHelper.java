package com.clonecoding.steam.utils;

import jakarta.servlet.http.Cookie;


public class TokenOperationHelper {

    public static Cookie createRefreshTokenCookie(String refreshToken, Long expireTime) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(Math.toIntExact(expireTime));
        refreshTokenCookie.setPath("/");
        return refreshTokenCookie;
    }
}
