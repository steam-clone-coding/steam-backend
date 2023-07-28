package com.clonecoding.steam.service;

import com.clonecoding.steam.auth.OAuth2UserInfo;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.LoginType;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.factory.OAuth2UserInfoFactory;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;

    /**
     * methodName : loadUser
     * Author : Junha
     * description : - 인증된 OAuth2 사용자를 불러오는 메서드
     *
     * @param : OAuth2UserRequest userRequest
     * @return : 찾거나 저장한 유저 반환
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return process(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * methodName : process
     * Author : Junha
     * description : - 유저의 OAuth2 인증 요청을 처리하고, 인증 정보를 바탕으로 사용자를 찾거나 생성하는 메서드
     *
     * @param : OAuth2UserRequest userRequest, OAuth2User oAuth2User
     * @return : 처리된 OAuth2User 객체 반환
     */
    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws Exception {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
        User user = findOrCreateUser(oAuth2UserInfo, provider);
        log.info("{}", user);

        return oAuth2User;
    }

    /**
     * methodName : findOrCreateUser
     * Author : Junha
     * description : - 주어진 OAuth2 사용자 정보를 바탕으로 DB에서 사용자를 찾고, 없는 경우 새 사용자를 생성하는 메서드
     *
     * @param : OAuth2UserInfo oAuth2UserInfo, String provider
     * @return : 찾거나 생성한 User 객체 반환
     */
    private User findOrCreateUser(OAuth2UserInfo oAuth2UserInfo, String provider) {
        Optional<User> optionalUser = userRepository.findUserByUid(oAuth2UserInfo.getUid());
        LoginType loginType = LoginType.fromString(provider);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            User user = User.builder()
                    .name(oAuth2UserInfo.getName())
                    .nickname("OAuth2TestUserNickName")
                    .age(oAuth2UserInfo.getAge())
                    .email("OAuth2Test@naver.com")
                    .profile_image(oAuth2UserInfo.getProfileImage())
                    .loginType(loginType)
                    .uid(oAuth2UserInfo.getUid())
                    .userRole(UserAuthority.ROLE_USER)
                    .build();
            log.info("user : {}", user);
            userRepository.save(user);
            return user;
        }

    }
}
