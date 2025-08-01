package com.wino.wino_api.service.user;

import com.wino.wino_api.dto.auth.UserSignupRequestDto;
import com.wino.wino_api.entity.user.UserInfo;
import com.wino.wino_api.repository.user.UserInfoRepository;
import com.wino.wino_api.security.AES256Util;
import lombok.RequiredArgsConstructor;
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
}
