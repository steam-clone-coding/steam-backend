package com.clonecoding.steam.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;



@ActiveProfiles("test")
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@ExtendWith(SpringExtension.class)
@Import(NanoIdProviderTest.TestConfig.class)
class NanoIdProviderTest {

    @Autowired
    NanoIdProvider nanoIdProvider;

    @Test
    @DisplayName("nanoIdProvider를 주입 받고 테스트할 수 있다.")
    void t1() throws Exception {
        assertThat(nanoIdProvider).isNotNull();

    }


    @RepeatedTest(value = 100)
    @DisplayName("설정한 길이의 nanoId를 얻을 수 있다.")
    void t2() throws Exception {
        assertThat(nanoIdProvider.createNanoId().length()).isEqualTo(10);

    }

    @RepeatedTest(value = 100)
    @DisplayName("항상 random한 nanoId를 얻을 수 있다.")
    void t3() throws Exception {
        String nanoId1 = nanoIdProvider.createNanoId();
        String nanoId2 = nanoIdProvider.createNanoId();

        assertThat(nanoId1).isNotEqualTo(nanoId2);

    }


    @RepeatedTest(value = 100)
    @DisplayName("nanoId는 알파벳과 숫자로만 구성되어 있다")
    void t4() throws Exception {
        String nanoId = nanoIdProvider.createNanoId();
        assertThat(nanoId).matches("^[A-Za-z0-9]{10}$");
    }

    @TestConfiguration
    public static class TestConfig{

        @Autowired
        private Environment environment;

        @Bean
        public NanoIdProvider nanoIdProvider(){
            return new NanoIdProvider(environment);
        }
    }


}