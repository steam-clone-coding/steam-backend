package com.clonecoding.steam.controller.common;

import com.clonecoding.steam.dto.mail.ValidateRequestDTO;
import com.clonecoding.steam.dto.mail.VerificationRequestDTO;
import com.clonecoding.steam.service.common.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;


    @Operation(summary = "메일 인증 코드 전송 API", description = "입력된 이메일로 인증코드를 전송하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 전송 성공시"),
            @ApiResponse(responseCode = "400", description = "이메일 정보가 존재하지 않을 시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 전송 실패시")
    })
    @PostMapping("/send-auth")
    public ResponseEntity<String> sendAuthCode(@RequestBody ValidateRequestDTO requestDTO) {
        String authCode = mailService.generateAndSendAuthCode(requestDTO.authMail());
        if (authCode != null) {
            return ResponseEntity.ok("인증코드가 성공적으로 발송되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("인증코드 발송에 실패했습니다. 다시 시도해주세요.");
        }
    }



    @Operation(summary = "메일 인증 API", description = "이메일을 통해 전달받은 인증코드를 입력해 인증하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 인증 성공시"),
            @ApiResponse(responseCode = "400", description = "인증 실패시"),
            @ApiResponse(responseCode = "500", description = "서버오류로 인한 인증 실패시")
    })
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
