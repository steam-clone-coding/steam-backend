package com.clonecoding.steam.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginDTO(
        String username,
        @Schema(example = "StringString123@@")
        String password) {
}
