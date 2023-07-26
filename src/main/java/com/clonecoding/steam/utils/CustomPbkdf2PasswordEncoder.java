package com.clonecoding.steam.utils;

import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class CustomPbkdf2PasswordEncoder extends Pbkdf2PasswordEncoder {


    private int iterations;
    private int saltLength;

    public CustomPbkdf2PasswordEncoder(CharSequence secret, int saltLength, int iterations, SecretKeyFactoryAlgorithm secretKeyFactoryAlgorithm) {
        super(secret, saltLength, iterations, secretKeyFactoryAlgorithm);

        this.iterations = iterations;
        this.saltLength = saltLength;
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
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String storedPassword = parts[1];

            // 사용자가 입력한 비밀번호를 암호화
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(), salt, iterations , saltLength * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] hashedPassword = skf.generateSecret(spec).getEncoded();

            // 위에서 암호화된 비밀번호와, 저장된 비밀번호를 비교
            String anObject = Base64.getEncoder().encodeToString(hashedPassword);


            if(!storedPassword.equals(anObject)){
                throw new UnAuthorizedException(ExceptionMessages.LOGIN_FAILURE.getMessage());
            }

            return true;
        } catch (Exception e) {
            // Handle exceptions properly
            throw new UnAuthorizedException(ExceptionMessages.LOGIN_FAILURE.getMessage(), e);
        }
    }

}