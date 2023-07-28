package com.clonecoding.steam.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class PasswordEncodeUtils {


    @Value("${spring.security.salt.algorithm}")
    private String SALT_CREATOR_ALGORITHM;

    @Value("${spring.security.pbkdf2.algorithm}")
    private String ENCODE_ALGORITHM;

    @Value("${spring.security.pbkdf2.hashwidth}")
    private Integer HASH_WIDTH;

    @Value("${spring.security.pbkdf2.iterations}")
    private Integer ITERATIONS;


    public String createSalt() throws NoSuchAlgorithmException {

        SecureRandom random = SecureRandom.getInstance(SALT_CREATOR_ALGORITHM);
        byte[] bytes = new byte[HASH_WIDTH];

        random.nextBytes(bytes);
        return new String(Base64.getEncoder().encode(bytes));
    }

    public String encodePassword(String rawPassword, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] saltByte = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), saltByte, ITERATIONS , HASH_WIDTH * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ENCODE_ALGORITHM);
        byte[] hashedPassword = skf.generateSecret(spec).getEncoded();

        // 위에서 암호화된 비밀번호와, 저장된 비밀번호를 비교
        return Base64.getEncoder().encodeToString(hashedPassword);

    }

}
