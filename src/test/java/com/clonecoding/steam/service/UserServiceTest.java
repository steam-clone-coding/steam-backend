package com.clonecoding.steam.service;

import com.clonecoding.steam.dto.UserRegisterDto;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.enums.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UserInfoConflictException;
import com.clonecoding.steam.repository.UserRepository;
import com.clonecoding.steam.utils.PasswordEncodeUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.*;



@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@Import(UserServiceTest.UserServiceTestConfig.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncodeUtils passwordEncodeUtils;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userRole(UserAuthority.ROLE_USER)
                .username("testUsername")
                .email("test@email.com")
                .uid("testuid")
                .build();

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
        final UserRegisterDto dto = UserRegisterDto.builder()
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
        final UserRegisterDto dto = UserRegisterDto.builder()
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
        final UserRegisterDto dto = UserRegisterDto.builder()
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

    //TODO : 회원가입 로직 완성시 활성화
    @Disabled
    @Test
    @DisplayName("비밀번호 조건, 이메일, username이 중복되지 않은 새로운 유저 정보를 저장할 수 있다.")
    void t4() throws Exception {
        //given
        final String testEmail = "registertest@email.com";
        final String testUsername = "registertest";
        final String testPassword = "a123456!";

        final UserRegisterDto dto = UserRegisterDto.builder()
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
    public static class UserServiceTestConfig{

        @Autowired
        private UserRepository userRepository;

        @Bean
        public PasswordEncodeUtils passwordEncodeUtils(){
            return new PasswordEncodeUtils();
        }

        @Bean
        public UserService userService(){
            return new UserService(userRepository, passwordEncodeUtils());
        }

    }
}