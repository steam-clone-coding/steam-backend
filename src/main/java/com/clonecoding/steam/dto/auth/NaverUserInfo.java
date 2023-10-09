package com.clonecoding.steam.dto.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;


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

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
}
