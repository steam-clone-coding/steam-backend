package com.clonecoding.steam.service.auth;


import com.clonecoding.steam.entity.user.User;
import com.clonecoding.steam.enums.user.UserAuthority;
import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import com.clonecoding.steam.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
@Component
public class PrincipalUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;


    /**
     * methodName : loadUserByUsername
     * Author : Minseok kim
     * description : userID를 기반으로 유저를 조회하고, UserDetail 객체로 변환해 리턴
     *
     * @param : String username-사용자의 ID(username)
     * @return : 찾은 유저를 UserDetail로 변환한 객체
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findUser = userRepository.findUserByUsername(username)
                .orElseThrow(()->new UnAuthorizedException(ExceptionMessages.LOGIN_FAILURE.getMessage()));


        // 여기서 CustomUserDetails를 생성해 리턴하면, PasswordEncoder에서 비밀번호가 일치한지 확인한다.
        // com.knlab.gps.utils.CustomPbkdf2PasswordEncoder 참고
        return new CustomUserDetails(findUser);
    }



    @RequiredArgsConstructor
    public class CustomUserDetails implements UserDetails{

        private final User user;

        public User getUser(){return user;}

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority(UserAuthority.ROLE_USER.name()));
        }

        @Override
        public String getPassword() {
            return String.format("%s:%s", user.getSalt(), user.getPassword());
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        /**
         * 계정 만료 여부
         * true : 만료 안됨
         * false : 만료
         * @return
         */
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        /**
         * 계정 잠김 여부
         * true : 잠기지 않음
         * false : 잠김
         * @return
         */
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        /**
         * 비밀번호 만료 여부
         * true : 만료 안됨
         * false : 만료
         * @return
         */
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }


        /**
         * 사용자 활성화 여부
         * ture : 활성화
         * false : 비활성화
         * @return
         */
        @Override
        public boolean isEnabled() {
            return true;
        }
    }



}
