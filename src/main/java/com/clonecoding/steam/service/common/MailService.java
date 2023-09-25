package com.clonecoding.steam.service.common;

import com.clonecoding.steam.repository.auth.AuthCodeRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class MailService {
    private final JavaMailSender mailSender;
    private final AuthCodeRepository authCodeRepository;

    public MailService(JavaMailSender mailSender, AuthCodeRepository authCodeRepository) {
        this.mailSender = mailSender;
        this.authCodeRepository = authCodeRepository;
    }

    public String getAuthCode(String authEmail) {
        String cachedCode = authCodeRepository.findAuthCode(authEmail);
        return cachedCode != null ? cachedCode : "인증코드가 만료되었습니다.";
    }

    public String generateAndSendAuthCode(String authEmail) {
        String authCode = generateAuthCode();
        authCodeRepository.saveAuthCode(authEmail, authCode);
        sendAuthEmail(authEmail, authCode);
        return authCode;
    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void sendAuthEmail(String toEmail, String authCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("이메일 인증 코드입니다.");
        message.setText("인증 코드: " + authCode);
        mailSender.send(message);
    }

    public String verifyAuthCode(String authEmail, String authCode) {
        String cachedCode = getAuthCode(authEmail);
        if ("인증코드가 만료되었습니다.".equals(cachedCode)) {
            return cachedCode;
        }
        return authCode.equals(cachedCode) && isValidEmailAddress(authEmail) ? "valid" : "invalid";
    }

    private boolean isValidEmailAddress(String email) {
        return email != null && email.contains("@");
    }
}