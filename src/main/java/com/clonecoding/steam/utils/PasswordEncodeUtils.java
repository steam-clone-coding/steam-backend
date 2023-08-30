package com.clonecoding.steam.utils;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class PasswordEncodeUtils {


    private final Environment environment;

    public String createSalt() throws NoSuchAlgorithmException {
        String saltCreatorAlgorithm = environment.getProperty("spring.security.salt.algorithm", String.class);
        Integer hashWidth = environment.getProperty("spring.security.pbkdf2.hashwidth", Integer.class);



        SecureRandom random = SecureRandom.getInstance(saltCreatorAlgorithm);
        byte[] bytes = new byte[hashWidth];

        random.nextBytes(bytes);
        return new String(Base64.getEncoder().encode(bytes));
    }

    public String encodePassword(String rawPassword, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String passwordEncodeAlgorithm = environment.getProperty("spring.security.pbkdf2.algorithm", String.class);
        Integer encodedDigestSize = environment.getProperty("spring.security.pbkdf2.digest-size", Integer.class);
        Integer iterations = environment.getProperty("spring.security.pbkdf2.iterations", Integer.class);


        byte[] saltByte = Base64.getDecoder().decode(salt);

        PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), saltByte, iterations, encodedDigestSize);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(passwordEncodeAlgorithm);
        byte[] hashedPassword = skf.generateSecret(spec).getEncoded();

        // 위에서 암호화된 비밀번호와, 저장된 비밀번호를 비교
        return Base64.getEncoder().encodeToString(hashedPassword);

    }

    public boolean verifyPassword(String plainTextPassword, String hashedPassword, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String calculatedHash = encodePassword(plainTextPassword, salt);
        return calculatedHash.equals(hashedPassword);
    }
}
