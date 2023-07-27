package com.clonecoding.steam.service;

import com.clonecoding.steam.auth.GoogleUserInfo;
import com.clonecoding.steam.auth.KakaoUserInfo;
import com.clonecoding.steam.auth.NaverUserInfo;
import com.clonecoding.steam.auth.OAuth2UserInfo;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.LoginType;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return this.process(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws Exception {
        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId();
        LoginType loginType = null;

        switch (provider) {
            case "google" -> {
                log.info("구글 로그인 요청");
                loginType = LoginType.GOOGLE;
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            }
            case "kakao" -> {
                log.info("카카오 로그인 요청");
                loginType = LoginType.KAKAO;
                oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            }
            case "naver" -> {
                log.info("네이버 로그인 요청");
                loginType = LoginType.NAVER;
                oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
            }
        }

        String providerId = oAuth2UserInfo.getUid();
        String name = oAuth2UserInfo.getName();
        String profileImage = oAuth2UserInfo.getProfileImage();
        String email = oAuth2UserInfo.getEmail();
        Integer age = oAuth2UserInfo.getAge();
        Optional<User> optionalUser = userRepository.findUserByUid(providerId);

        User user = null;

        if(optionalUser.isEmpty()) {
            user = User.builder()
                    .name(name)
                    .age(age)
                    .email(email)
                    .profile_image(profileImage)
                    .loginType(loginType)
                    .uid(providerId)
                    .userRole(UserAuthority.ROLE_USER)
                    .username("Test")
                    .build();
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        return oAuth2User;
    }

}