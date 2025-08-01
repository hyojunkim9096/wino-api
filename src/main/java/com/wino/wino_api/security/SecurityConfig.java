package com.wino.wino_api.security;

import com.wino.wino_api.security.jwt.JwtAuthenticationFilter;
import com.wino.wino_api.security.jwt.JwtExceptionFilter;
import com.wino.wino_api.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

/**
 * Spring Security 전체 설정을 담당하는 클래스
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * JWT 인증 필터 빈 등록
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * JWT 예외 필터 빈 등록
     */
    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter();
    }

    /**
     * Spring Security FilterChain 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (JWT 기반 API 서버에선 비활성화)
                .csrf(csrf -> csrf.disable())

                // 세션 사용하지 않음 (JWT는 무상태 인증 방식)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 경로별 접근 허용 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/favicon.ico",
                                "/flutter/**",
                                "/main.dart.js", "/flutter.js",
                                "/assets/**",
                                "/api/auth/**",
                                "/css/**", "/js/**", "/img/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 로그인/HTTP Basic 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // JWT 관련 필터 등록
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // 인증 필터 실행
                .addFilterBefore(jwtExceptionFilter(), JwtAuthenticationFilter.class) // 예외 처리 필터 실행

                // 보안 헤더 설정
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .addHeaderWriter(new StaticHeadersWriter("X-Content-Type-Options", "nosniff"))
                )

                // 인증 실패 시 401 Unauthorized 응답
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                );

        return http.build();
    }
}