package com.clonecoding.steam.exceptions;

public class EncodeException extends RuntimeException {
    public EncodeException(String message, Throwable cause) {
        super(message, cause);
    }
    public EncodeException(String message) {
        super(message);
    }
}



