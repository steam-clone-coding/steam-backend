package com.clonecoding.steam.controller.common;

import com.clonecoding.steam.dto.mail.ValidateRequestDTO;
import com.clonecoding.steam.dto.mail.VerificationRequestDTO;
import com.clonecoding.steam.service.common.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send-auth")
    public ResponseEntity<String> sendAuthCode(@RequestBody ValidateRequestDTO requestDTO) {
        String authCode = mailService.generateAndSendAuthCode(requestDTO.authMail());
        if (authCode != null) {
            return ResponseEntity.ok("인증코드가 성공적으로 발송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증코드 발송에 실패했습니다. 다시 시도해주세요.");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAuthCode(@RequestBody VerificationRequestDTO requestDTO) {
        String authResult = mailService.verifyAuthCode(requestDTO.authEmail(), requestDTO.authCode());
        if ("valid".equals(authResult)) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.badRequest().body("인증 실패");
        }
    }
}
