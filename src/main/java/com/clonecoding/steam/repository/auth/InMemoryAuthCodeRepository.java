package com.clonecoding.steam.repository.auth;

import com.clonecoding.steam.repository.auth.AuthCodeRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryAuthCodeRepository implements AuthCodeRepository {
    private final Map<String, String> authCodes = new ConcurrentHashMap<>();

    @Override
    public String findAuthCode(String authEmail) {
        return authCodes.getOrDefault(authEmail, "인증코드가 만료되었습니다.");
    }

    @Override
    public void saveAuthCode(String authEmail, String authCode) {
        authCodes.put(authEmail, authCode);
    }
}
