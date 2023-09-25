package com.clonecoding.steam.repository.auth;

public interface AuthCodeRepository {
    String findAuthCode(String authEmail);

    void saveAuthCode(String authEmail, String authCode);
}