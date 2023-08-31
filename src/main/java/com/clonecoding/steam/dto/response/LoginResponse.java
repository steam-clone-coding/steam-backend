package com.clonecoding.steam.dto.response;


import lombok.Builder;
import lombok.Data;

// RefreshToken 재발급의 경우에만 사용
@Builder
@Data
public class LoginResponse {
    private String uid;
    private String accessToken;
}
