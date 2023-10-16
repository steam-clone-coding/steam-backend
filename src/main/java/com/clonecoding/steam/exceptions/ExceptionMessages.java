package com.clonecoding.steam.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionMessages {
    INVALID_TOKEN("Access 토큰이 올바르지 않습니다."),
    INVALID_TOKEN_TYPE("Access 토큰 타입이 올바르지 않습니다."),
    EXPIRED_TOKEN("Access 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN("Refresh 토큰이 올바르지 않습니다."),
    EXPIRED_REFRESH_TOKEN("Refresh 토큰이 만료되었습니다."),
    REFRESH_WITH_NOT_EXPIRED_TOKEN("만료되지 않은 Access토큰을 통해 재발급을 수행하려고 합니다."),
    NO_REFRESH_TOKEN("Refresh 토큰이 쿠키에 존재하지 않습니다."),
    LOGIN_FAILURE("아이디 또는 비밀번호가 잘못되었습니다."),
    EMAIL_DUPLICATED("이메일이 중복됩니다."),
    INVALID_PASSWORD("비밀번호가 최소 8자, 하나 이상의 문자, 하나의 숫자 및 하나의 특수 문자를 포함해야합니다."),
    USERNAME_DUPLICATED("username이 중복됩니다."),
    EMPTY_ARRAY("빈 배열입니다."),
    IMAGE_SERVER_PROCESS_FAILED("이미지 서버와의 통신 중 오류가 있습니다"),
    USER_NOT_FOUND("username에 맞는 사용자를 찾을 수 없습니다"),
    PASSWORD_NOT_FOUND("비밀번호에 맞는 사용자가 존재하지 않습니다."),
    PASSWORD_ENCODING_FAILED("비밀번호 인코딩에 실패하였습니다."),
    USER_CREATION_FAILED("사용자 생성에 실패하였습니다."),
    GAME_NOT_FOUND("해당하는 게임을 찾을 수 없습니다."),
    ORDER_NOT_FOUND("해당하는 주문 정보를 찾을 수 없습니다.");
    private String message;

}
