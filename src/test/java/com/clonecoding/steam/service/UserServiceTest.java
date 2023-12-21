package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.request.UserRegisterDTO;
import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.user.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UserInfoConflictException;
import com.clonecoding.steam.repository.user.UserRepository;
import com.clonecoding.steam.service.common.RedisService;
import com.clonecoding.steam.service.user.UserService;
import com.clonecoding.steam.service.user.impl.UserServiceImpl;
import com.clonecoding.steam.utils.auth.JwtTokenProvider;
import com.clonecoding.steam.utils.common.NanoIdProvider;
import com.clonecoding.steam.utils.user.PasswordEncodeUtils;

import com.clonecoding.steam.utils.user.UserValidator;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@Import(UserServiceTest.UserServiceTestConfig.class)
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncodeUtils passwordEncodeUtils;

    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        // 사용자 정보 설정
        testUser = User.builder()
                .userRole(UserAuthority.ROLE_USER)
                .username("testUsername")
                .password("testPassword")
                .email("test@email.com")
                .uid("testuid")
                .build();

        String salt = passwordEncodeUtils.createSalt();
        String encodedPassword = passwordEncodeUtils.encodePassword(testUser.getPassword(), salt);
        testUser.setSalt(salt);
        testUser.setPassword(encodedPassword);

        // 유저 저장
        userRepository.save(testUser);


    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("중복된 이메일을 가진 유저가 이미 가입되어 있다면 Exception을 throw한다.")
    void t1() throws Exception {
        //given
        final UserRegisterDTO dto = UserRegisterDTO.builder()
                .email(testUser.getEmail())
                .username(testUser.getUsername() + "aa")
                .build();
        //when & then
        assertThatThrownBy(()->userService.register(dto))
                .isInstanceOf(UserInfoConflictException.class)
                .hasMessage(ExceptionMessages.EMAIL_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("중복된 username을 가진 유저가 이미 가입되어 있다면 Exception을 throw 한다.")
    void t2() throws Exception {
        //given
        final UserRegisterDTO dto = UserRegisterDTO.builder()
                .email(testUser.getEmail()  + "aa")
                .username(testUser.getUsername())
                .build();
        //when & then
        assertThatThrownBy(()->userService.register(dto))
                .isInstanceOf(UserInfoConflictException.class)
                .hasMessage(ExceptionMessages.USERNAME_DUPLICATED.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"aa,false", "aaaaaaaa,false", "12345678,false", "a1234567,false", "a123456!,true"})
    @DisplayName("비밀번호가 최소 8자, 하나 이상의 문자, 하나의 숫자 및 하나의 특수 문자를 포함하지 않으면 오류를 throw한다.")
    void t3(String password, boolean expected) throws Exception {
        //given
        final UserRegisterDTO dto = UserRegisterDTO.builder()
                .email("passwordTest@email.com")
                .username("passwordTestUsername")
                .password(password)
                .build();
        //when & then
        if(expected){
            assertThatCode(()->userService.register(dto)).doesNotThrowAnyException();
            return;
        }
        assertThatThrownBy(()->userService.register(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessages.INVALID_PASSWORD.getMessage());
    }



    @Test
    @DisplayName("비밀번호 조건, 이메일, username이 중복되지 않은 새로운 유저 정보를 저장할 수 있다.")
    void t4() throws Exception {
        //given
        final String testEmail = "registertest@email.com";
        final String testUsername = "registertest";
        final String testPassword = "a123456!";


        final UserRegisterDTO dto = UserRegisterDTO.builder()
                .email(testEmail)
                .username(testUsername)
                .password(testPassword)
                .build();

        //when
        userService.register(dto);

        //then
        final User findUser = userRepository.findUserByEmail(testEmail)
                .orElseThrow(() -> new RuntimeException("유저가 저장되지 않았습니다."));
        final String encodedPassword = passwordEncodeUtils.encodePassword(testPassword, findUser.getSalt());

        assertThat(findUser).extracting("email", "username", "password")
                .containsExactly(testEmail, testUsername, encodedPassword);
    }

    @TestConfiguration
    @DataRedisTest
    public static class UserServiceTestConfig {
        @Autowired
        private Environment environment;


        @Bean
        public PasswordEncodeUtils passwordEncodeUtils() {

            return new PasswordEncodeUtils(environment);
        }

        @Bean
        public NanoIdProvider nanoIdProvider() {
            return new NanoIdProvider(environment);
        }

        @Bean
        public RedisService redisService(RedisTemplate<String, String> redisTemplate) {
            return new RedisService(redisTemplate);
        }

        @Bean
        public JwtTokenProvider jwtTokenProvider(RedisService redisService) {
            return new JwtTokenProvider(environment);
        }

        @Bean
        public UserValidator userValidator(UserRepository userRepository) {
            return new UserValidator(userRepository);
        }

        @Bean
        public UserService userService(UserRepository userRepository, PasswordEncodeUtils passwordEncodeUtils,
                        JwtTokenProvider jwtTokenProvider, NanoIdProvider nanoIdProvider,
                        UserValidator userValidator) {
            return new UserServiceImpl(userRepository, passwordEncodeUtils, nanoIdProvider, jwtTokenProvider,  userValidator);
        }
    }
}
