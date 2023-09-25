package com.clonecoding.steam.dto.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
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
        return (String) attributes.get("email");
    }

    @Override
    public Integer getAge() {
        return null;
    }


    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("picture");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}