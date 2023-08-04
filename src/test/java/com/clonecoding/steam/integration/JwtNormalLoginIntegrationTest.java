package com.clonecoding.steam.integration;


import com.clonecoding.steam.dto.UserRegisterDto;
import com.clonecoding.steam.dto.request.LoginRequest;
import com.clonecoding.steam.enums.LoginType;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.notNullValue;
/**
* @className JwtNormalLoginIntegrationTest
* @author : Minseok Kim
* @description 일반 로그인 & 회원가입에 대한 통합테스트
**/

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class JwtNormalLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Bearer 토큰 없이 인증이 필요한 uri 접근시 401 코드를 리턴한다.")
    void t1() throws Exception {
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/health-check"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }


    //TODO : 회원가입 로직 완성시 활성화
    @Disabled
    @Test
    @DisplayName("Normal 로그인 성공시 200 코드와 함께 JWT 토큰, 리프레시 토큰, uid를 리턴한다.")
    void t2() throws Exception {
        //given
        // 로그인 하기 위해 먼저 회원가입 진행
        final UserRegisterDto testUser = UserRegisterDto.builder()
                .email("testEmail@naver.com")
                .username("test")
                .password("a1234567!")
                .loginType(LoginType.NORMAL)
                .age(22)
                .userRole(UserAuthority.ROLE_USER)
                .countryId(null)
                .profileImage(null)
                .build();

        userService.register(testUser);
        
        // 그 후 요청 생성
        final LoginRequest testLoginRequestBody = LoginRequest.builder()
                .username("test")
                .password("a1234567!").build();
        

        //when & then
        // 200Code와 함께, accessToken, refreshToken, uid를 리턴하는지 확인
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/login")
                        .content(objectMapper.writeValueAsString(testLoginRequestBody))
        )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.uid", notNullValue()));
    }




    @Test
    @DisplayName("Normal 로그인시 존재하지않는 username이라면 로그인 실패 메시지와 401오류를 리턴한다.")
    void t3() throws Exception {
        //given
        LoginRequest testLoginRequestBody = LoginRequest.builder()
                .username("test")
                .password("a1234567!").build();
        //when & then
        // 401 Code와 함께 에러 메시지를 리턴하는지 확인
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/login")
                                .content(objectMapper.writeValueAsString(testLoginRequestBody))
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ExceptionMessages.LOGIN_FAILURE.getMessage()));
    }

    //TODO : 회원가입 로직 완성시 활성화
    @Disabled
    @Test
    @DisplayName("Normal 로그인시 비밀번호가 잘못되었다면 로그인실패 메시지와 401오류를 리턴한다.")
    void t4() throws Exception {
        //given
        // 로그인 하기 위해 먼저 회원가입 진행
        final UserRegisterDto testUser = UserRegisterDto.builder()
                .email("testEmail@naver.com")
                .username("test")
                .password("a1234567!")
                .loginType(LoginType.NORMAL)
                .age(22)
                .userRole(UserAuthority.ROLE_USER)
                .countryId(null)
                .profileImage(null)
                .build();

        userService.register(testUser);

        // 그 후 요청 생성
        final LoginRequest testLoginRequestBody = LoginRequest.builder()
                .username("test")
                .password("a").build();


        //when & then
        // 401 Code와 함께 에러 메시지를 리턴하는지 확인
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/login")
                                .content(objectMapper.writeValueAsString(testLoginRequestBody))
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ExceptionMessages.LOGIN_FAILURE.getMessage()));
    }

}
