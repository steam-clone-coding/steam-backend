package com.clonecoding.steam.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Import(PasswordEncodeUtilsTest.TestConfig.class)
public class PasswordEncodeUtilsTest {

    @Autowired
    private PasswordEncodeUtils passwordEncodeUtils;

    @Value("${spring.security.pbkdf2.hashwidth}")
    private Integer saltLength;

    @Test
    @DisplayName("salt를 생성할 수 있다.")
    void t1() throws Exception {
        //given

        //when
        String salt = passwordEncodeUtils.createSalt();
        //then
        assertThat(salt.endsWith("==")).isTrue();
    }




    @TestConfiguration
    public static class TestConfig{

        @Bean
        public PasswordEncodeUtils passwordEncodeUtils(){
            return new PasswordEncodeUtils();
        }
    }
}