package com.clonecoding.steam.utils;

import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ExtendWith(SpringExtension.class)
@Import(JwtTokenProviderTest.TestConfig.class)
public class JwtTokenProviderTest {



    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    private Long testRefreshTokenExpireTime = 1800000L;
    private Long testAccessTokenExpireTime = 600000L;

    @Test
    @DisplayName("JwtTokenProvider를 주입 받고 테스트할 수 있다.")
    void t1() throws Exception {
        //then

        Long accessTokenExpireTime = (Long) ReflectionTestUtils
                .getField(jwtTokenProvider, "ACCESS_TOKEN_EXPIRE_TIME");

        Long refreshTokenExpireTime = (Long) ReflectionTestUtils
                .getField(jwtTokenProvider, "REFRESH_TOKEN_EXPIRE_TIME");

        assertThat(jwtTokenProvider).isNotNull();

        assertThat(accessTokenExpireTime).isEqualTo(testAccessTokenExpireTime);
        assertThat(refreshTokenExpireTime).isEqualTo(testRefreshTokenExpireTime);
    }

    @Test
    @DisplayName("User 정보를 담는  Access Token을 생성하고 다시 해독해 이를 조회할 수 있다.")
    void t2() throws Exception {
        //given
        User testUser = User.builder()
                .uid("JMBGR6CDT3")
                .userRole(UserAuthority.ROLE_USER)
                .username("testUserId")
                .build();
        //when

        Date now = new Date();
        String accessToken = jwtTokenProvider.sign(testUser, now);

        JwtTokenProvider.TokenVerificationResult result = jwtTokenProvider.verify(accessToken);

        //then
        assertThat(result).extracting("uid", "userID", "userRole")
                .containsExactly("JMBGR6CDT3", "testUserId", UserAuthority.ROLE_USER);

    }

    @Test
    @DisplayName("AccessToken이 만료되면 ExpiredJwtException이 throw된다.")
    void t3() throws Exception {
        //given
        User testUser = User.builder()
                .id(1L)
                .userRole(UserAuthority.ROLE_USER)
                .username("Hello")
                .build();
        //when
        Date prevTokenDate = new Date(0L); //아주 옛날에 생성된 엑세스 토큰..
        String accessToken = jwtTokenProvider.sign(testUser, prevTokenDate);

        ThrowableAssert.ThrowingCallable throwableMethod = ()->jwtTokenProvider.verify(accessToken);

        //then
        assertThatThrownBy(throwableMethod).isInstanceOf(ExpiredJwtException.class);

    }

    @Test
    @DisplayName("잘못된 토큰 값을 Verify하면 JwtException을 Throw한다.")
    void t4() throws Exception {
        //given
        String invalidToken = "te1_asd24AD142awWQf_31342QWW2";
        //when
        ThrowableAssert.ThrowingCallable throwableMethod = ()->jwtTokenProvider.verify(invalidToken);
        //then
        assertThatThrownBy(throwableMethod).isInstanceOf(JwtException.class)
                .hasMessage(ExceptionMessages.INVALID_TOKEN.getMessage());
    }


    @Test
    @DisplayName("Jwt Token으로 부터 유저 정보를 받아 Authentication 객체를 생성할 수 있다.")
    void t5() throws Exception {
        //given
        User testUser = User.builder()
                .id(1L)
                .userRole(UserAuthority.ROLE_USER)
                .username("Hello")
                .uid("test")
                .build();

        Date now = new Date();
        String accessToken = jwtTokenProvider.sign(testUser, now);
        //when
        final Authentication authentication = jwtTokenProvider.getAuthentication(testUser.getUid(),testUser.getUserRole());
        //then
        assertThat(authentication.getPrincipal()).isEqualTo(testUser.getUid());
        assertThat(authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(testUser.getUserRole().name()))).isTrue();

    }


    @TestConfiguration
    public static class TestConfig{

        @Autowired
        private Environment environment;

        @Bean
        public JwtTokenProvider jwtTokenProvider(){
            return new JwtTokenProvider(environment);
        }
    }
}

