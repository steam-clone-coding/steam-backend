package com.clonecoding.steam.utils;

import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class CustomPasswordEncoder extends Pbkdf2PasswordEncoder {


    private final PasswordEncodeUtils passwordEncodeUtils;

    public CustomPasswordEncoder(CharSequence secret, int saltLength, int iterations, SecretKeyFactoryAlgorithm secretKeyFactoryAlgorithm, PasswordEncodeUtils passwordEncodeUtils) {
        super(secret, saltLength, iterations, secretKeyFactoryAlgorithm);
        this.passwordEncodeUtils = passwordEncodeUtils;
    }

    /**
     * methodName : matches
     * Author : Minseok Kim
     * description : 비밀번호가 일치하는지 확인하는 메서드
     *
     * @param rawPassword - 사용자가 입력한 비밀번호
     * @param encodedPassword - DB에 저장된 비밀번호 : 토큰:비밀번호 형태로 넘겨받도록되어있음(CustomUserDetails.getPasswords() 참고)
     * @return true면 비밀번호 일치, false면 불일치
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
            // salt와 저장된 비밀번호 분리
            String[] parts = encodedPassword.split(":");
            String salt = parts[0];
            String storedPassword = parts[1];

            // 사용자가 입력한 비밀번호를 암호화
            String hashedPassword = passwordEncodeUtils.encodePassword(storedPassword, salt);

            // 위에서 암호화된 비밀번호와, 저장된 비밀번호를 비교

            if(!storedPassword.equals(hashedPassword)){
                throw new UnAuthorizedException(ExceptionMessages.LOGIN_FAILURE.getMessage());
            }

            return true;
        } catch (Exception e) {
            // Handle exceptions properly
            throw new UnAuthorizedException(ExceptionMessages.LOGIN_FAILURE.getMessage(), e);
        }
    }

}