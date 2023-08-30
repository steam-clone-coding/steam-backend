package com.clonecoding.steam.controller;

import com.clonecoding.steam.IntegrationTestSupport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class OAuth2ControllerTest extends IntegrationTestSupport {
    @Autowired
    private  OAuth2Controller controller;

//    private static ClientAndServer mockServer;
//
//    @BeforeAll
//    static void beforeAll() {
//        mockServer = startClientAndServer(1080);
//    }
//
//    @BeforeEach
//    public void setUp() {
//        mockServer.reset();
//    }
//
//
//    @AfterAll
//    static void afterAll() {
//        mockServer.close();
//    }

    @DisplayName("카카오 로그인 요청이 왔을 때 해당 uri로 리다이렉트 할 수 있다.")
    @Test
    public void testKakaoLogin() throws IOException {
        //given
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        //when
        controller.kakaoLogin(mockResponse);

        //then
        assertThat("/oauth2/authorization/kakao").isEqualTo(mockResponse.getRedirectedUrl());
    }

    @DisplayName("네이버 로그인 요청이 왔을 때 해당 uri로 리다이렉트 할 수 있다.")
    @Test
    public void testNaverLogin() throws IOException {
        //given
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        //when
        controller.naverLogin(mockResponse);

        //then
        assertThat("/oauth2/authorization/naver").isEqualTo(mockResponse.getRedirectedUrl());
    }

    @DisplayName("구글 로그인 요청이 왔을 때 해당 uri로 리다이렉트 할 수 있다.")
    @Test
    public void testGoogleLogin() throws IOException {
        //given
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();

        //when
        controller.googleLogin(mockResponse);

        //then
        assertThat("/oauth2/authorization/google").isEqualTo(mockResponse.getRedirectedUrl());
    }

}