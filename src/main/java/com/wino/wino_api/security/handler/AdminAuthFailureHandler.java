// src/main/java/com/wino/wino_api/security/handler/AdminAuthFailureHandler.java
package com.wino.wino_api.security.handler;

import com.wino.wino_api.repository.admin.AdminUserInfoRepository;

import com.wino.wino_api.security.audit.AdminAuthLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 로그인 실패 시 후처리 핸들러
 * - 실패 원인에 따라 사용자에게 전달할 에러 코드를 결정
 * - 실패 횟수 증가 및 계정 잠금 처리
 * - 실패 로그(admin_auth_log) 저장
 *
 * 설정 예) application.properties
 *   security.login.max-failed=5          # 실패 n회 이상일 때 잠금
 *   security.login.lock-minutes=15       # 잠금 유지 시간(분)
 *
 * 주의: SecurityConfig에서 usernameParameter("userId")로 맞춰주세요.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AdminUserInfoRepository repo;
    private final AdminAuthLogService logService;

    /** 실패 허용 횟수 (기본 5회) */
    @Value("${security.login.max-failed:5}")
    private int maxFailed;

    /** 잠금 유지 시간(분) (기본 15분) */
    @Value("${security.login.lock-minutes:15}")
    private int lockMinutes;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        // 로그인 폼의 파라미터명: userId (프로젝트 기준)
        String userId = request.getParameter("userId");
        if (userId == null || userId.isBlank()) {
            // 혹시 폼이 다를 수 있어 대비
            userId = request.getParameter("username");
        }

        // 1) 예외 유형을 분류해서 사용자에게 보낼 쿼리 파라미터 결정
        //    (프런트/템플릿에서 ?error= 값으로 분기)
        String errorParam;

        if (exception instanceof DisabledException) {
            // 비활성화 계정: TEMPORARY(임시) 상태라면 별도 코드, 아니면 disabled
            errorParam = "disabled";
            if (userId != null && !userId.isBlank()) {
                String finalUserId = userId;
                repo.findByUserId(finalUserId).ifPresent(u -> {
                    if ("TEMPORARY".equalsIgnoreCase(u.getStatus())) {
                        // 임시 계정(예: 최초 비밀번호 변경 필요 등)
                        setDefaultFailureUrl("/admin/login?error=temporary");
                    } else {
                        setDefaultFailureUrl("/admin/login?error=disabled");
                    }
                });
            } else {
                setDefaultFailureUrl("/admin/login?error=disabled");
            }

            // 실패 로그 기록
            logService.logLoginFailure(userId, "DISABLED_OR_TEMPORARY", request);
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        if (exception instanceof LockedException) {
            // 이미 잠겨 있는 계정
            setDefaultFailureUrl("/admin/login?error=locked");
            logService.logLoginFailure(userId, "ACCOUNT_LOCKED", request);
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        if (exception instanceof CredentialsExpiredException || exception instanceof AccountExpiredException) {
            // 자격/계정 만료
            setDefaultFailureUrl("/admin/login?error=expired");
            logService.logLoginFailure(userId, "EXPIRED", request);
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        if (exception instanceof BadCredentialsException) {
            // 아이디/비밀번호 불일치
            AtomicBoolean lockedNow = new AtomicBoolean(false);

            if (userId != null && !userId.isBlank()) {
                String finalUserId = userId;
                repo.findByUserId(finalUserId).ifPresent(u -> {
                    int fail = (u.getFailedLoginAttempts() == null ? 0 : u.getFailedLoginAttempts()) + 1;
                    u.setFailedLoginAttempts(fail);

                    // 임계치 이상이면 잠금 처리 (상태 + 잠금 만료 시각)
                    if (fail >= maxFailed && !"LOCKED".equalsIgnoreCase(u.getStatus())) {
                        u.setStatus("LOCKED");
                        u.setAccountLockedUntil(LocalDateTime.now().plusMinutes(lockMinutes));
                        lockedNow.set(true);
                    }
                    repo.save(u);
                });
            }

            // 프런트용 에러 파라미터 결정
            errorParam = lockedNow.get() ? "locked" : "bad";
            setDefaultFailureUrl("/admin/login?error=" + errorParam);

            // 실패 로그 기록 (잠금 직후라면 별도 reason)
            logService.logLoginFailure(userId, lockedNow.get() ? "LOCKED_AFTER_MAX_FAILURES" : "BAD_CREDENTIALS", request);

            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        // 기타 알 수 없는 실패
        setDefaultFailureUrl("/admin/login?error=bad");
        logService.logLoginFailure(userId, "UNKNOWN", request);
        super.onAuthenticationFailure(request, response, exception);
    }
}
