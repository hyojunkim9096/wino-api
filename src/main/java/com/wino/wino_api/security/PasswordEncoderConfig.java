package com.wino.wino_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {
    // PasswordEncoder를 Bean으로 등록
    // BCrypt는 해시 알고리즘 중 하나로, 매번 다른 Salt를 사용해 안전함

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 내부적으로 salt와 10회의 반복 해시 처리
    }
}
