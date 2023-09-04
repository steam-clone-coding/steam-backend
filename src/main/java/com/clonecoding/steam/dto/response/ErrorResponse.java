package com.clonecoding.steam.dto.response;

import lombok.Data;

import java.util.Arrays;

@Data
public class ErrorResponse {

    public ErrorResponse(Exception e) {
        this.message = e.getMessage();
        this.details = Arrays.toString(e.getStackTrace());
    }

    private String message;
    private String details;
}
