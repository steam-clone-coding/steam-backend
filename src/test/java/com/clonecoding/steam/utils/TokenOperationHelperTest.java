package com.clonecoding.steam.utils;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.clonecoding.steam.utils.auth.TokenOperationHelper.createRefreshTokenCookie;
import static org.assertj.core.api.Assertions.assertThat;

class TokenOperationHelperTest {

    @DisplayName("응답 쿠키에 refresh token을 담을 수 있다.")
    @Test
    void createRefreshTokenCookie1(){
        //given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2OTMzMzc5NjgsImV4cCI6MTY5MzMzNzk4OH0.0CWVdILm8FBrheqgLs3bu0V0p85sS9FS5ot2qoxXuKU";
        Long expireTime = 20000L;

        //when
        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken, expireTime);

        //then
        assertThat(refreshTokenCookie.getValue()).isEqualTo(refreshToken);
    }
}