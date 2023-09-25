package com.clonecoding.steam.service.user;

import com.clonecoding.steam.dto.request.UserRegisterDTO;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.auth.LoginType;
import com.clonecoding.steam.exceptions.EncodeException;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.user.UserRepository;
import com.clonecoding.steam.utils.auth.JwtTokenProvider;
import com.clonecoding.steam.utils.common.NanoIdProvider;
import com.clonecoding.steam.utils.user.PasswordEncodeUtils;
import com.clonecoding.steam.utils.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    // 상수와 멤버 변수
    private static final String DEFAULT_IMAGE_PATH = "http://49.50.163.215/images/e4065d551a514f9e9cf2edd21a3a5ff1";
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
}

