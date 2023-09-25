package com.clonecoding.steam.repository;

import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("test-user")
                .email("hello@naver.com")
                .uid("testuid")
                .build();

        userRepository.save(testUser);
    }


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("username으로 유저를 조회할 수 있다.")
    void t1() throws Exception {
        //given

        //when
        final User user = userRepository.findUserByUsername(testUser.getUsername())
                .orElseThrow(() -> new RuntimeException("유저를 찾아야 합니다."));

        //then
        assertThat(user).extracting("username", "email", "uid")
                .containsExactly(testUser.getUsername(), testUser.getEmail(), testUser.getUid());
    }

    @Test
    @DisplayName("UID로 유저를 조회할 수 있다.")
    void t2() throws Exception {
        //given

        //when
        User user = userRepository.findUserByUid(testUser.getUid())
                .orElseThrow(() -> new RuntimeException("유저를 찾아야 합니다."));

        //then
        assertThat(user).extracting("username", "email", "uid")
                .containsExactly(testUser.getUsername(), testUser.getEmail(), testUser.getUid());
    }


    @Test
    @DisplayName("email로 유저를 조회할 수 있다.")
    void t3() throws Exception {
        //given

        //when
        User user = userRepository.findUserByEmail(testUser.getEmail())
                .orElseThrow(() -> new RuntimeException("유저를 찾아야 합니다."));

        //then
        assertThat(user).extracting("username", "email", "uid")
                .containsExactly(testUser.getUsername(), testUser.getEmail(), testUser.getUid());
    }
}