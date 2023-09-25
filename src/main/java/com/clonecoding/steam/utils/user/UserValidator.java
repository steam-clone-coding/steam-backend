package com.clonecoding.steam.utils.user;

import com.clonecoding.steam.exceptions.UserInfoConflictException;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.dto.request.UserRegisterDTO;
import com.clonecoding.steam.repository.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validate(UserRegisterDTO dto) {
        validateUsername(dto.getUsername());
        validateEmail(dto.getEmail());
        validatePassword(dto.getPassword());
    }

    private void validateUsername(String username) {
        if (userRepository.findUserByUsername(username).isPresent()) {
            throw new UserInfoConflictException(ExceptionMessages.USERNAME_DUPLICATED.getMessage());
        }
    }

    private void validateEmail(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new UserInfoConflictException(ExceptionMessages.EMAIL_DUPLICATED.getMessage());
        }
    }

    private void validatePassword(String password) {
        if (!Pattern.matches(
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                password
        )) {
            throw new IllegalArgumentException(ExceptionMessages.INVALID_PASSWORD.getMessage());
        }
    }
}

