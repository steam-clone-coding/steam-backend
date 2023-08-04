package com.clonecoding.steam.auth;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserInfo extends OAuth2User {
    String getProfileImage();
    String getName();
    String getUid();
    String getEmail();
    Integer getAge();
}
