package com.clonecoding.steam.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.assertj.core.api.Assertions.*;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@Import(PasswordEncodeUtilsTest.PasswordEncodeUtilsTestConfig.class)
public class PasswordEncodeUtilsTest {

    @Autowired
    private PasswordEncodeUtils passwordEncodeUtils;


    @Test
    @DisplayName("salt를 생성할 수 있다.")
    void t1() throws Exception {
        //given

        //when
        String salt = passwordEncodeUtils.createSalt();
        //then
        assertThat(salt.endsWith("==")).isTrue();
    }

    @RepeatedTest(value = 50)
    @DisplayName("비밀번호를 암호화할 수 있다. 이 때, salt와 비밀번호가 같으면 항상 같은 암호화된 비밀번호가 나온다.")
    void t2() throws Exception {
        //given
        String testPassword = "1111";
        String salt = passwordEncodeUtils.createSalt();
        //when
        String testPw1 = passwordEncodeUtils.encodePassword(testPassword, salt);
        String testPw2 = passwordEncodeUtils.encodePassword(testPassword, salt);

        //then
        assertThat(testPw1).isEqualTo(testPw2);
    }


    @TestConfiguration
    public static class PasswordEncodeUtilsTestConfig {

        @Bean
        public PasswordEncodeUtils passwordEncodeUtils(){
            return new PasswordEncodeUtils();
        }
    }
}