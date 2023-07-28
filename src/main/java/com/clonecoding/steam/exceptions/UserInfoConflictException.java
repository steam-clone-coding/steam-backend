package com.clonecoding.steam.exceptions;

public class UserInfoConflictException extends RuntimeException{

    public UserInfoConflictException(String message) {
        super(message);
    }

    public UserInfoConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
