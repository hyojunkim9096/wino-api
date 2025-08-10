// src/main/java/com/wino/wino_api/security/handler/AdminAuthFailureHandler.java
package com.wino.wino_api.security.handler;

import com.wino.wino_api.repository.admin.AdminUserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class AdminAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AdminUserInfoRepository repo;
    private static final int MAX_FAILED = 5;
    private static final int LOCK_MINUTES = 15; // 시간잠금 유지 시간

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String code = "bad";
        String userId = request.getParameter("userId");

        if (exception instanceof DisabledException) {
            if (userId != null && !userId.isBlank()) {
                repo.findByUserId(userId).ifPresent(u -> {
                    if ("TEMPORARY".equalsIgnoreCase(u.getStatus())) {
                        setDefaultFailureUrl("/admin/login?error=temporary");
                    } else {
                        setDefaultFailureUrl("/admin/login?error=disabled");
                    }
                });
            } else {
                setDefaultFailureUrl("/admin/login?error=disabled");
            }
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        if (exception instanceof LockedException) {
            setDefaultFailureUrl("/admin/login?error=locked");
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        if (exception instanceof CredentialsExpiredException || exception instanceof AccountExpiredException) {
            setDefaultFailureUrl("/admin/login?error=expired");
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        if (exception instanceof BadCredentialsException) {
            AtomicBoolean lockedNow = new AtomicBoolean(false);
            if (userId != null && !userId.isBlank()) {
                repo.findByUserId(userId).ifPresent(u -> {
                    int fail = (u.getFailedLoginAttempts() == null ? 0 : u.getFailedLoginAttempts()) + 1;
                    u.setFailedLoginAttempts(fail);
                    if (fail >= MAX_FAILED && !"LOCKED".equalsIgnoreCase(u.getStatus())) {
                        u.setStatus("LOCKED");
                        u.setAccountLockedUntil(LocalDateTime.now().plusMinutes(LOCK_MINUTES)); // 시간잠금
                        lockedNow.set(true);
                    }
                    repo.save(u);
                });
            }
            setDefaultFailureUrl("/admin/login?error=" + (lockedNow.get() ? "locked" : "bad"));
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        setDefaultFailureUrl("/admin/login?error=" + code);
        super.onAuthenticationFailure(request, response, exception);
    }
}
