package com.clonecoding.steam.service;


import com.clonecoding.steam.dto.UserRegisterDto;
import com.clonecoding.steam.exceptions.UserInfoConflictException;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.UserRepository;
import com.clonecoding.steam.utils.PasswordEncodeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncodeUtils passwordEncoder;


    public void register(UserRegisterDto dto){
        // username, email Validate
        if(isUsernamePresent(dto.getUsername())){
            throw new UserInfoConflictException(ExceptionMessages.USERNAME_DUPLICATED.getMessage());
        }
        if(isEmailPresent(dto.getEmail())){
            throw new UserInfoConflictException(ExceptionMessages.EMAIL_DUPLICATED.getMessage());
        }

        // 비밀번호가 최소 8자, 하나 이상의 문자, 하나의 숫자 및 하나의 특수 문자를 포함하는지 확인
        if(!Pattern.matches(
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                dto.getPassword()
        )){
            throw new IllegalArgumentException(ExceptionMessages.INVALID_PASSWORD.getMessage());
        }



    }

    private boolean isEmailPresent(String email) {
        return userRepository.findUserByEmail(email).isPresent();

    }

    private boolean isUsernamePresent(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }
}