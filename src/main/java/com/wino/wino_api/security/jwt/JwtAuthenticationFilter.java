// src/main/java/com/wino/wino_api/security/jwt/JwtAuthenticationFilter.java
package com.wino.wino_api.security.jwt;

import com.wino.wino_api.security.admin.AdminUserDetails;
import com.wino.wino_api.service.admin.AdminUserAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminUserAuthService adminUserAuthService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AdminUserAuthService adminUserAuthService) {
        this.jwtUtil = jwtUtil;
        this.adminUserAuthService = adminUserAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getServletPath();

        // 관리자 웹은 폼로그인 처리 → JWT 패스
        if (path.startsWith("/admin")) {
            chain.doFilter(request, response);
            return;
        }
        // 주로 API에만 JWT 적용 (필요 시 조건 조정)
        if (!path.startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        // 핵심: 매니저 authenticate() 호출하지 않음
        String userId = jwtUtil.validateAndGetUsername(token);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AdminUserDetails user = (AdminUserDetails) adminUserAuthService.loadUserByUsername(userId);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }
}
