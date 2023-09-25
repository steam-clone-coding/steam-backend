package com.clonecoding.steam.service;
import com.clonecoding.steam.dto.auth.OAuth2UserInfo;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.repository.user.UserRepository;
import com.clonecoding.steam.service.auth.OAuth2AuthenticationSuccessHandler;
import com.clonecoding.steam.service.common.RedisService;
import com.clonecoding.steam.utils.auth.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler authenticationSuccessHandler;

    @BeforeEach
    public void setUp() {
        given(jwtTokenProvider.sign(any(), any())).willReturn("mockAccessToken");
        given(jwtTokenProvider.createRefresh(any())).willReturn("mockRefreshToken");
    }

    @DisplayName("로그인에 성공을 처리할 수 있다. 성공시 access토큰과 refresh 토큰이 각 1번 발급된다")
    @Test
    public void testOnAuthenticationSuccess() throws Exception {
        // given
        OAuth2UserInfo mockOAuth2UserInfo = mock(OAuth2UserInfo.class);
        given(mockOAuth2UserInfo.getEmail()).willReturn("test@email.com");

        User mockUser = mock(User.class);
        given(mockUser.getUid()).willReturn("mockUid");

        given(userRepository.findUserByEmail(anyString())).willReturn(java.util.Optional.of(mockUser));

        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(mockOAuth2UserInfo, null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, mockAuthentication);

        // then
        verify(jwtTokenProvider, times(1)).sign(any(), any());
        verify(jwtTokenProvider, times(1)).createRefresh(any());
    }
}