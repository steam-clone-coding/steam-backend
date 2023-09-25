package com.clonecoding.steam.dto.request;


import com.clonecoding.steam.enums.auth.LoginType;
import com.clonecoding.steam.enums.user.UserAuthority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserRegisterDTO {

    private String name;

    private String nickname;

    private Long countryId;

    private Integer age;

    private String email;

    @Builder.Default
    private LoginType loginType = LoginType.NORMAL;

    private String profileImage;

    private String username;

    @Schema(example = "StringString123@@")
    private String password;

    @Builder.Default
    private UserAuthority userRole = UserAuthority.ROLE_USER;
}