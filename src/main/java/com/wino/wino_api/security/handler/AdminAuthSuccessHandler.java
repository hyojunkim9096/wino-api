// src/main/java/com/wino/wino_api/security/handler/AdminAuthSuccessHandler.java
package com.wino.wino_api.security.handler;

import com.wino.wino_api.repository.admin.AdminUserInfoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class AdminAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AdminUserInfoRepository repo;

    public AdminAuthSuccessHandler(AdminUserInfoRepository repo,
                                   String defaultTargetUrl,
                                   boolean alwaysUseDefaultTargetUrl) {
        this.repo = repo;
        setDefaultTargetUrl(defaultTargetUrl);
        // ✅ 메서드명 수정: setAlwaysUseDefaultTargetUrl(...)
        setAlwaysUseDefaultTargetUrl(alwaysUseDefaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 로그인 성공 시 last_login 갱신 + 실패횟수 0으로 리셋
        String userId = authentication.getName();
        repo.findByUserId(userId).ifPresent(u -> {
            u.setLastLogin(LocalDateTime.now());
            if (u.getFailedLoginAttempts() != null && u.getFailedLoginAttempts() > 0) {
                u.setFailedLoginAttempts(0);
            }
            repo.save(u);
        });

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
