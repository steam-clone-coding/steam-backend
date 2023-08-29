package com.clonecoding.steam.service;

import com.clonecoding.steam.entity.Dummy;
import com.clonecoding.steam.entity.User;
import com.clonecoding.steam.repository.DummyRepository;
import com.clonecoding.steam.repository.UserRepository;
import com.clonecoding.steam.utils.CustomPbkdf2PasswordEncoder;
import com.clonecoding.steam.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CustomPbkdf2PasswordEncoder passwordEncoder;

    public void registerUser(String username, String password) {
        // 사용자 이름(username)이 이미 존재하는지 확인하고, 존재한다면 예외 처리할 수도 있습니다.
        // 이 예시에서는 간단히 중복 확인 없이 진행합니다.

        // 패스워드를 암호화하여 저장
        String encodedPassword = encodePassword(password);

        // 새로운 사용자 생성
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .build();

        // 사용자 정보를 데이터베이스에 저장
        userRepository.save(user);
    }

    private String encodePassword(String password) {
        // ":" 문자를 구분자로 사용하여 저장된 비밀번호와 솔트 값을 조합한 후 인코딩
        String salt = passwordEncoder.generateSalt(); // 이 부분은 salt를 생성하는 방법에 따라 구현해야 합니다.
        String encodedPassword = salt + ":" + passwordEncoder.encode(salt + password);
        return encodedPassword;
    }


    public String loginUser(String username, String password) {
        // 사용자 이름(username)으로 사용자를 데이터베이스에서 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));


        // 입력한 비밀번호와 데이터베이스에 저장된 비밀번호를 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인이 성공하면 Access Token을 생성하여 반환
        Date now = new Date();
        String accessToken = jwtTokenProvider.sign(user, now);

        return accessToken;
    }
}
