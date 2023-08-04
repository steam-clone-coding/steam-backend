package com.clonecoding.steam.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;
    private Map<String, Object> attributesProperties;
    private Map<String, Object> attributesAccount;

    public KakaoUserInfo(Map<String,Object> attributes){
        this.attributes = attributes;
        this.attributesProperties = (Map<String, Object>) attributes.get("properties");
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
    }


    @Override
    public String getUid() {
        return  String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) attributesAccount.get("email");
    }

    @Override
    public Integer getAge() {
        return  (Integer)((Map) attributes.get("properties")).get("age_range");
    }

    @Override
    public String getName() {return (String) attributesProperties.get("nickname"); }

    @Override
    public String getProfileImage(){return (String) attributesProperties.get("profile_image"); }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
