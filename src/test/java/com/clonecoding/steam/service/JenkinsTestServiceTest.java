package com.clonecoding.steam.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class JenkinsTestServiceTest {

    private JenkinsTestService jenkinsTestService;

    @BeforeEach
    void setUp() {
        jenkinsTestService = new JenkinsTestService();
    }



    @Disabled
    @Test
    @DisplayName("젠킨스용 테스트 : 테스트를 성공한다")
    void t1() throws Exception {
        final int i = 1;
        assertThat(i).isEqualTo(1);
    }

    @Test
    @DisplayName("젠킨스용 테스트 : 테스트를 실패한다.")
    void t2() throws Exception {
        fail("젠킨스 테스트 실패");
    }

}