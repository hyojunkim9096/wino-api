package com.wino.wino_api.security;

import com.wino.wino_api.security.handler.AdminAuthFailureHandler;
import com.wino.wino_api.security.handler.AdminAuthSuccessHandler;
import com.wino.wino_api.security.handler.AdminLogoutSuccessHandler;
import com.wino.wino_api.security.session.AdminSessionExpiredStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final AdminAuthSuccessHandler adminAuthSuccessHandler;
    private final AdminAuthFailureHandler adminAuthFailureHandler;
    private final AdminLogoutSuccessHandler adminLogoutSuccessHandler;
    private final AdminSessionExpiredStrategy adminSessionExpiredStrategy;

    /** 별도 Config(SessionSupportConfig)에서 제공됨 → 순환 참조 방지 */
    private final SessionRegistry sessionRegistry;

    @Value("${security.session.max-sessions:1}")
    private int maxSessions;

    @Value("${security.session.prevent-second-login:false}")
    private boolean preventSecondLogin;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/sign/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sf -> sf.migrateSession())
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(maxSessions)
                                .maxSessionsPreventsLogin(preventSecondLogin)      // false → 두 번째 로그인 허용 & 기존 세션 만료
                                .expiredSessionStrategy(adminSessionExpiredStrategy)
                                .sessionRegistry(sessionRegistry)                  // ← 분리된 빈 주입
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/admin/login",
                                "/admin/sign/**",
                                "/favicon.ico",
                                "/images/**", "/css/**", "/js/**", "/assets/**",
                                "/", "/index.html", "/error"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .successHandler(adminAuthSuccessHandler)
                        .failureHandler(adminAuthFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessHandler(adminLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .headers(headers -> headers
                        .frameOptions(f -> f.sameOrigin())
                        .addHeaderWriter(new StaticHeadersWriter("X-Content-Type-Options", "nosniff"))
                );

        return http.build();
    }
}
