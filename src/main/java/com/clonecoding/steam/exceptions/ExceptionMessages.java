package com.clonecoding.steam.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionMessages {

    INVALID_TOKEN("엑세스 토큰이 올바르지 않습니다."),
    INVALID_TOKEN_TYPE("엑세스 토큰 타입이 올바르지 않습니다."),
    EXPIRED_TOKEN("토큰이 만료되었습니다."),
    LOGIN_FAILURE("아이디 또는 비밀번호가 잘못되었습니다."),
    EMAIL_DUPLICATED("이메일이 중복됩니다."),
    USERNAME_DUPLICATED("username이 중복됩니다."),
    IMAGE_SERVER_PROCESS_FAILED("이미지 서버와의 통신 중 오류가 있습니다");
    private String message;


}
