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

import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    // 상수와 멤버 변수
    private static final String DEFAULT_IMAGE_PATH = "profile.jpg";
    private static final LoginType DEFAULT_LOGIN_TYPE = LoginType.NORMAL;

    private final UserRepository userRepository;
    private final PasswordEncodeUtils passwordEncoder;
    private final NanoIdProvider nanoIdProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserValidator userValidator;


    // 사용자 등록
    public void register(UserRegisterDTO dto) {
        userValidator.validate(dto);
        User newUser = createUser(dto);
        userRepository.save(newUser);
    }

    // 로그인
    public LoginResponse login(UserLoginDTO dto) {
        User user = findUserByUsername(dto.username());
        validatePassword(dto, user);
        return generateLoginResponse(user);
    }

    private User createUser(UserRegisterDTO dto) {
        String[] encryptedData = encryptPassword(dto.getPassword());
        return buildUser(dto, encryptedData[0], encryptedData[1]);
    }

    // User 객체를 생성
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

    // 비밀번호 암호화
    private String[] encryptPassword(String password) {
        try {
            return doEncryptPassword(password);
        } catch (Exception e) {
            throw new EncodeException(ExceptionMessages.PASSWORD_ENCODING_FAILED.getMessage());
        }
    }

    // 실제 암호화 로직
    private String[] doEncryptPassword(String password) throws Exception {
        String salt = passwordEncoder.createSalt();
        String encodedPassword = passwordEncoder.encodePassword(password, salt);
        return new String[]{encodedPassword, salt};
    }

    // UID 생성
    private String generateUID() {
        return nanoIdProvider.createNanoId();
    }

    // 사용자 이름으로 사용자 찾기
    private User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UnAuthorizedException(ExceptionMessages.USER_NOT_FOUND.getMessage()));
    }

    // 비밀번호 검증
    private void validatePassword(UserLoginDTO dto, User user) {
        if (!verifyPassword(dto, user)) {
            throw new UnAuthorizedException(ExceptionMessages.PASSWORD_NOT_FOUND.getMessage());
        }
    }

    // 사용자 비밀번호 검증
    private boolean verifyPassword(UserLoginDTO dto, User user) {
        try {
            return passwordEncoder.verifyPassword(dto.password(), user.getPassword(), user.getSalt());
        } catch (Exception e) {
            throw new EncodeException(ExceptionMessages.PASSWORD_ENCODING_FAILED.getMessage());
        }
    }


    // 로그인 응답 생성
    private LoginResponse generateLoginResponse(User user) {
        Date now = new Date();
        String accessToken = jwtTokenProvider.sign(user, now);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .uid(user.getUid())
                .build();
    }
}

