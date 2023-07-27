package com.clonecoding.steam.auth;

import com.clonecoding.steam.entity.User;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;


    @Override
    public User toUser() {
        return null;
    }

    @Override
    public String getUid() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public Integer getAge() {

        return  LocalDate.now().getYear() - Integer.parseInt(String.valueOf(attributes.get("birthyear")));
    }


    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImage(){
        return (String) (attributes.get("profile_image"));
    }
}
