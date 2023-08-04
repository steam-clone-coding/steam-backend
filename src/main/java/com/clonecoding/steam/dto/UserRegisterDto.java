package com.clonecoding.steam.dto;


import com.clonecoding.steam.entity.Country;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.entity.UserWallet;
import com.clonecoding.steam.enums.LoginType;
import com.clonecoding.steam.enums.UserAuthority;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserRegisterDto {

    private String name;

    private Long countryId;

    private Integer age;

    private String email;

    @Builder.Default
    private LoginType loginType = LoginType.NORMAL;

    private String profileImage;

    private String username;

    private String password;

    @Builder.Default
    private UserAuthority userRole = UserAuthority.ROLE_USER;



    public User toEntity(String uid, String encodedPassword, String salt, UserWallet wallet, Country country){
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .salt(salt)
                .uid(uid)
                .wallet(wallet)
                .profileImage(null)
                .name(name)
                .loginType(loginType)
                .lastLoginTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .country(country)
                .userRole(userRole)
                .email(email)
                .loginType(loginType)
                .build();
    }

}
