package com.clonecoding.steam.service;


import com.clonecoding.steam.dto.UserRegisterDto;
import com.clonecoding.steam.exceptions.UserInfoConflictException;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.UserRepository;
import com.clonecoding.steam.utils.PasswordEncodeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncodeUtils passwordEncoder;


    public void register(UserRegisterDto dto){
        if(isUsernamePresent(dto.getUsername())){
            throw new UserInfoConflictException(ExceptionMessages.USERNAME_DUPLICATED.getMessage());
        }
        if(isEmailPresent(dto.getEmail())){
            throw new UserInfoConflictException(ExceptionMessages.EMAIL_DUPLICATED.getMessage());
        }



    }

    private boolean isEmailPresent(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    private boolean isUsernamePresent(String username) {
        return userRepository.findUserByUsername(username).isPresent();
    }




}
