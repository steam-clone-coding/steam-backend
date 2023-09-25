package com.clonecoding.steam.service.auth;

import com.clonecoding.steam.dto.auth.OAuth2UserInfo;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.auth.LoginType;
import com.clonecoding.steam.factory.auth.OAuth2UserInfoFactory;
import com.clonecoding.steam.repository.user.UserRepository;
import com.clonecoding.steam.utils.common.NanoIdProvider;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static com.clonecoding.steam.entity.user.User.createUser;
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


    private void saveUserIfNotExist(OAuth2UserInfo oAuth2UserInfo, String provider) {
        Optional<User> optionalUser = userRepository.findUserByEmail(oAuth2UserInfo.getEmail());

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(oAuth2UserInfo.getName());
            existingUser.setProfile_image(oAuth2UserInfo.getProfileImage());
            existingUser.setLastLoginTime(LocalDateTime.now());
        } else {
            User newUser = createUser(
                    oAuth2UserInfo.getName(),
                    oAuth2UserInfo.getEmail(),
                    oAuth2UserInfo.getProfileImage(),
                    LoginType.fromString(provider),
                    nanoIdProvider.createNanoId()
            );
            userRepository.save(newUser);
        }
    }

}