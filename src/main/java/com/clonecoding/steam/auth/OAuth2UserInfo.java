package com.clonecoding.steam.auth;

import com.clonecoding.steam.entity.User;

public interface OAuth2UserInfo {
    String getProfileImage();
    String getName();
    String getUid();
    String getEmail();
    Integer getAge();
    User toUser();
}
