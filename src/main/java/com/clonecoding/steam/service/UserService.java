package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.request.UserLoginDTO;
import com.clonecoding.steam.dto.request.UserRegisterDTO;
import com.clonecoding.steam.dto.response.LoginResponse;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.LoginType;
import com.clonecoding.steam.exceptions.EncodeException;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.UserRepository;
import com.clonecoding.steam.utils.JwtTokenProvider;
import com.clonecoding.steam.utils.NanoIdProvider;
import com.clonecoding.steam.utils.PasswordEncodeUtils;
import com.clonecoding.steam.utils.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private static final String DEFAULT_IMAGE_PATH = "profile.jpg";
    private static final LoginType DEFAULT_LOGIN_TYPE = LoginType.NORMAL;

    private final UserRepository userRepository;
    private final PasswordEncodeUtils passwordEncoder;
    private final NanoIdProvider nanoIdProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserValidator userValidator;

    public void register(UserRegisterDTO dto) {
        userValidator.validate(dto);
        User newUser = createUser(dto);
        userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(UserLoginDTO req) {
        User user = findUserByUsername(req.username());
        validatePassword(req, user);

        return generateLoginResponse(user);
    }

    private User createUser(UserRegisterDTO dto) {
        String[] encryptedData = encryptPassword(dto.getPassword());
        return buildUser(dto, encryptedData[0], encryptedData[1]);
    }

    private User buildUser(UserRegisterDTO dto, String encodedPassword, String salt) {
        return User.builder()
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .name(dto.getName())
                .age(dto.getAge())
                .userRole(dto.getUserRole())
                .password(encodedPassword)
                .salt(salt)
                .loginType(DEFAULT_LOGIN_TYPE)
                .uid(generateUID())
                .profile_image(DEFAULT_IMAGE_PATH)
                .build();
    }

    private String[] encryptPassword(String password) {
        try {
            String salt = passwordEncoder.createSalt();
            String encodedPassword = passwordEncoder.encodePassword(password, salt);
            return new String[]{encodedPassword, salt};
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncodeException("Password encoding failed", e);
        }
    }

    private String generateUID() {
        return nanoIdProvider.createNanoId();
    }

    private User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UnAuthorizedException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
    }

    private void validatePassword(UserLoginDTO req, User user) {
        boolean isPasswordMatch;
        try {
            isPasswordMatch = passwordEncoder.verifyPassword(req.Password(), user.getPassword(), user.getSalt());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new EncodeException("Password encoding failed", e);
        }

        if (!isPasswordMatch) {
            throw new UnAuthorizedException(ExceptionMessages.PASSWORD_NOT_FOUND.getMessage());
        }
    }

    private LoginResponse generateLoginResponse(User user) {
        Date now = new Date();
        String accessToken = jwtTokenProvider.sign(user, now);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .uid(user.getUid())
                .build();
    }
}
