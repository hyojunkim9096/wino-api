// src/main/java/com/wino/wino_api/security/SecurityConfig.java
package com.wino.wino_api.security;

import com.wino.wino_api.repository.admin.AdminUserInfoRepository;
import com.wino.wino_api.security.handler.AdminAuthFailureHandler;
import com.wino.wino_api.security.handler.AdminAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AdminUserInfoRepository adminRepo) throws Exception {

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/sign/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/sign/**").permitAll()
                        .requestMatchers(
                                "/admin/login",
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
                        .successHandler(new AdminAuthSuccessHandler(adminRepo, "/admin/board/adminMainBoard", true))
                        .failureHandler(new AdminAuthFailureHandler(adminRepo))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .headers(headers -> headers
                        .frameOptions(f -> f.sameOrigin())
                        .addHeaderWriter(new StaticHeadersWriter("X-Content-Type-Options", "nosniff"))
                );

        return http.build();
    }
}
