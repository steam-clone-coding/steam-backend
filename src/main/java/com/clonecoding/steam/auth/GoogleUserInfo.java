package com.clonecoding.steam.auth;

import com.clonecoding.steam.entity.User;
import lombok.AllArgsConstructor;

import java.util.Map;
@AllArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    @Override
    public String getUid() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public Integer getAge() {
        return null;
    }


    @Override
    public User toUser() {
        return null;
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImage() {
        return null;
    }
}
