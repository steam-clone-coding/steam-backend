package com.clonecoding.steam.auth;

import com.clonecoding.steam.entity.User;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;

    @Override
    public String getUid() {
        return  String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) ((Map) attributes.get("properties")).get("account_email");
    }

    @Override
    public Integer getAge() {
        return  (Integer)((Map) attributes.get("properties")).get("age_range");
    }

    @Override
    public String getName() {
        // kakao_account라는 Map에서 추출
        return (String) ((Map) attributes.get("properties")).get("nickname");
    }

    @Override
    public String getProfileImage(){
        return (String) ((Map) attributes.get("properties")).get("profile_image");
    }

    @Override
    public User toUser() {
        return null;
    }
}
