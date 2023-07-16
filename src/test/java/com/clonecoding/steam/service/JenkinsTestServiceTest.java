package com.clonecoding.steam.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class JenkinsTestServiceTest {

    private JenkinsTestService jenkinsTestService;

    @BeforeEach
    void setUp() {
        jenkinsTestService = new JenkinsTestService();
    }


    @Test
    @DisplayName("젠킨스용 테스트 : 테스트를 성공한다")
    void t1() throws Exception {
        final int i = 1;
        assertThat(i).isEqualTo(1);
    }
}