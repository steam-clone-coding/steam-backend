package com.clonecoding.steam.service;

import com.clonecoding.steam.auth.OAuth2UserInfo;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.LoginType;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.factory.OAuth2UserInfoFactory;
import com.clonecoding.steam.repository.UserRepository;
import com.clonecoding.steam.utils.NanoIdProvider;
import jakarta.transaction.Transactional;
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
@Transactional
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2UserInfoFactory oAuth2UserInfoFactory;
    private final NanoIdProvider nanoIdProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            String provider = userRequest.getClientRegistration().getRegistrationId();
            OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());
            saveUserIfNotExist(oAuth2UserInfo, provider);

            return oAuth2UserInfo;
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }


    void saveUserIfNotExist(OAuth2UserInfo oAuth2UserInfo, String provider) {
        Optional<User> optionalUser = userRepository.findUserByEmail(oAuth2UserInfo.getEmail());
        LoginType loginType = LoginType.fromString(provider);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(oAuth2UserInfo.getName());
            existingUser.setProfileImage(oAuth2UserInfo.getProfileImage());
            log.info("User updated: {}", existingUser);
        } else {
            User newUser = User.builder()
                    .name(oAuth2UserInfo.getName())
                    .nickname("OAuth2TestUserNickName")
                    .age(null)
                    .email(oAuth2UserInfo.getEmail())
                    .profileImage(oAuth2UserInfo.getProfileImage())
                    .loginType(loginType)
                    .uid(nanoIdProvider.createNanoId())
                    .userRole(UserAuthority.ROLE_USER)
                    .build();
            userRepository.save(newUser);
            log.info("User created: {}", newUser);
        }

    }

}