package com.clonecoding.steam.config;


import com.clonecoding.steam.filter.CustomUsernamePasswordAuthenticationFilter;
import com.clonecoding.steam.filter.ExceptionHandlerFilter;
import com.clonecoding.steam.filter.JwtAuthenticationFilter;
import com.clonecoding.steam.service.PrincipalOauth2UserService;
import com.clonecoding.steam.utils.CustomPbkdf2PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CorsConfigurationSource corsConfigurationSource;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final PrincipalOauth2UserService oauth2UserService;
    private final AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private static final RequestMatcher LOGIN_REQUEST_MATCHER = new AntPathRequestMatcher("/api/login","POST");


    //TODO: hashWidth, ITERATIONS, HASHING ALGORITHM은 어떻게 하는게 제일 효율적일까?
    @Value("${spring.security.pbkdf2.hashwidth}")
    private Integer HASH_WIDTH;

    @Value("${spring.security.pbkdf2.iterations}")
    private Integer ITERATIONS;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
        return new CustomUsernamePasswordAuthenticationFilter(LOGIN_REQUEST_MATCHER, authenticationManager(authenticationConfiguration));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new CustomPbkdf2PasswordEncoder("", HASH_WIDTH, ITERATIONS, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA512);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider
                = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors-> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests((authorizeHttpRequest)->{
                    authorizeHttpRequest
                            .requestMatchers("/api/login").permitAll()
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**" ).permitAll()
                            .anyRequest().permitAll();
                })
                .oauth2Login(oauth2Login->{
                    oauth2Login
                            .authorizationEndpoint(config->config.baseUri("/oauth2/authorization"))
                            .userInfoEndpoint(userInfo->userInfo.userService(oauth2UserService))
                            .successHandler(oAuth2AuthenticationSuccessHandler);
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)  //remember me disable
                // 권한이 없는 사용자가 url 요청시 아래의 오류 리턴
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint))
                //JWT토큰 사용에 따른 session disable
                .sessionManagement(sessionManagement->{
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                // 로그인 핸들러 추가(로그인 url : /api/login)
                .addFilterAt(usernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 로그인 실패시 호출되는 Exception Filter 추가 : 로그인 실패시 아래 필터에서 Response 응답 후 리턴
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, jwtAuthenticationFilter.getClass())
                .build();
    }


}