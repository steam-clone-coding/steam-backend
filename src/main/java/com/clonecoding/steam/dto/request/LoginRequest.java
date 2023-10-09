package com.clonecoding.steam.dto.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LoginRequest {

    private String username;

    private String password;
}
