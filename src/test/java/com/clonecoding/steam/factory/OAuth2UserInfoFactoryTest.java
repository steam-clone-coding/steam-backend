package com.clonecoding.steam.factory;

import com.clonecoding.steam.IntegrationTestSupport;
import com.clonecoding.steam.auth.GoogleUserInfo;
import com.clonecoding.steam.auth.KakaoUserInfo;
import com.clonecoding.steam.auth.NaverUserInfo;
import com.clonecoding.steam.auth.OAuth2UserInfo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;


class OAuth2UserInfoFactoryTest extends IntegrationTestSupport {

    @Autowired
    private OAuth2UserInfoFactory oAuth2UserInfoFactory;

    private static Stream<Arguments> provideProviderTypesForCreatingUserInfo(){

        return Stream.of(
                Arguments.of("kakao",  mock(KakaoUserInfo.class)),
                Arguments.of("google", mock(GoogleUserInfo.class)),
                Arguments.of("naver",  mock(NaverUserInfo.class))
        );
    }

    @DisplayName("요청하는 provider에 해당하는 UserInfo를 생성할 수 있다.")
    @MethodSource("provideProviderTypesForCreatingUserInfo")
    @ParameterizedTest
    void getOAuth2UserInfo(String provider, OAuth2UserInfo expected){

        //given
        Map<String, Object> attributes = new HashMap<>();

        //when

        OAuth2UserInfo oAuth2UserInfo = oAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);

        //then
        assertThat(oAuth2UserInfo).isInstanceOf(expected.getClass());

    }

    @DisplayName("카카오, 구글, 네이버가 아닌 provider의 UserInfo를 생성할 수 없다.")
    @Test
    void getOAuth2UserInfoWithWrongProvider(){

        //given
        String provider = "Instagram";
        Map<String, Object> attributes = new HashMap<>();

        //when & then
        assertThatThrownBy(() -> oAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 OAuth2 공급자 입니다. : " + provider);

    }
}
