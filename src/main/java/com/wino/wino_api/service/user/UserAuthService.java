package com.wino.wino_api.service.user;

import com.wino.wino_api.dto.auth.UserLoginRequestDto;
import com.wino.wino_api.dto.auth.UserSignupRequestDto;
import com.wino.wino_api.entity.user.UserInfo;
import com.wino.wino_api.repository.user.UserInfoRepository;
import com.wino.wino_api.security.AES256Util;
import com.wino.wino_api.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 사용자 인증 서비스
 */
@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 처리
     */
    public void register(UserSignupRequestDto request) throws Exception {
        if (userInfoRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        UserInfo user = UserInfo.builder()
                .email(AES256Util.encrypt(request.getEmail()))
                .password(passwordEncoder.encode(request.getPassword()))
                .name(AES256Util.encrypt(request.getName()))
                .phoneNumber(AES256Util.encrypt(request.getPhoneNumber()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userInfoRepository.save(user);
    }

    /**
     * 로그인 처리
     */
    public String login(UserLoginRequestDto request) throws Exception {
        UserInfo user = userInfoRepository.findByEmail(AES256Util.encrypt(request.getEmail()))
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성 또는 세션 처리
        return jwtUtil.createToken(AES256Util.decrypt(user.getEmail()), user.getId());

    }

}
