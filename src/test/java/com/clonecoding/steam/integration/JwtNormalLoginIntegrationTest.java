package com.clonecoding.steam.integration;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
* @className JwtNormalLoginIntegrationTest
* @author : Minseok Kim
* @description 일반 로그인에 대한 통합테스트
**/

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class JwtNormalLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("Bearer 토큰 없이 인증이 필요한 uri 접근시 401 코드를 리턴한다.")
    void t1() throws Exception {
        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/health-check"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

}
