package com.clonecoding.steam.factory;

import com.clonecoding.steam.auth.GoogleUserInfo;
import com.clonecoding.steam.auth.KakaoUserInfo;
import com.clonecoding.steam.auth.NaverUserInfo;
import com.clonecoding.steam.auth.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class OAuth2UserInfoFactory {
    public OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google" -> {
                log.info("구글 로그인 요청");
                return new GoogleUserInfo(attributes);
            }
            case "kakao" -> {
                log.info("카카오 로그인 요청");
                return new KakaoUserInfo(attributes);
            }
            case "naver" -> {
                log.info("네이버 로그인 요청");
                return new NaverUserInfo((Map<String, Object>) attributes.get("response"));
            }
            default -> throw new IllegalArgumentException("잘못된 OAuth2 공급자 입니다. : " + provider);
        }
    }
}