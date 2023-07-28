package com.clonecoding.steam.service;


import com.clonecoding.steam.dto.UserRegisterDto;
import com.clonecoding.steam.exceptions.ConflictException;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void register(UserRegisterDto dto){
        if(isUsernamePresent(dto)){
            throw new ConflictException(ExceptionMessages.USERNAME_DUPLICATED.getMessage());
        }


    }

    private boolean isUsernamePresent(UserRegisterDto dto) {
        return userRepository.findUserByUsername(dto.getUsername()).isPresent();
    }




}
