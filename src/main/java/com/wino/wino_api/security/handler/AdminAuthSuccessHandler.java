// src/main/java/com/wino/wino_api/security/handler/AdminAuthSuccessHandler.java
package com.wino.wino_api.security.handler;

import com.wino.wino_api.repository.admin.AdminUserInfoRepository;

import com.wino.wino_api.security.audit.AdminAuthLogService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 로그인 성공 시 후처리 핸들러
 * - 동일 아이디의 '다른' 세션들을 즉시 만료(expireNow) 처리(프로퍼티로 on/off)
 * - 이 세션만 유휴 타임아웃(기본 60분) 적용
 * - 만료 예정 시각을 세션 속성에 저장(프런트 카운트다운 용)
 * - 로그인 성공 로그 저장
 * - 관리자 계정의 last_login 갱신 및 실패횟수 초기화
 *
 * 설정 예) application.properties
 *  security.login.success.default-target=/admin/board/adminMainBoard
 *  security.login.success.always-use-default=true
 *  security.session.timeout-seconds=3600
 *  security.session.kill-previous-on-login=true  # 동일 아이디 기존 세션 즉시 만료
 */
@Component
@RequiredArgsConstructor
public class AdminAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /** 인증 로그 저장 서비스 */
    private final AdminAuthLogService logService;

    /** 관리자 계정 정보 저장소 */
    private final AdminUserInfoRepository repo;

    /** 세션 레지스트리 (동시 세션 제어/탐색용) */
    private final SessionRegistry sessionRegistry;

    /** 로그인 성공 후 이동할 기본 URL (미설정시 /admin/board/adminMainBoard) */
    @Value("${security.login.success.default-target:/admin/board/adminMainBoard}")
    private String defaultTargetUrlProp;

    /** 저장된 요청과 무관하게 항상 defaultTargetUrl 로 이동할지 여부 (기본 true) */
    @Value("${security.login.success.always-use-default:true}")
    private boolean alwaysUseDefaultUrlProp;

    /** 세션 유휴 만료 시간(초) - 기본 3600초(60분) */
    @Value("${security.session.timeout-seconds:3600}")
    private int sessionTimeoutSeconds;

    /** 동일 아이디 기존 세션을 즉시 만료할지 여부 (기본 true) */
    @Value("${security.session.kill-previous-on-login:true}")
    private boolean killPreviousOnLogin;

    /** 빈 초기화 시 핸들러의 기본 동작을 프로퍼티로 세팅 */
    @PostConstruct
    public void init() {
        setDefaultTargetUrl(defaultTargetUrlProp);
        setAlwaysUseDefaultTargetUrl(alwaysUseDefaultUrlProp);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // 현재 세션 생성/확보 및 유휴 타임아웃 적용
        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(sessionTimeoutSeconds);

        // 동일 아이디의 '다른' 세션들을 즉시 만료(expireNow)
        if (killPreviousOnLogin) {
            expireOtherSessions(authentication.getName(), session.getId());
        }

        // 프런트에서 카운트다운 초기화에 사용할 만료 시각 저장 (epochMillis)
        long expiresAt = System.currentTimeMillis() + (sessionTimeoutSeconds * 1000L);
        session.setAttribute("SESSION_TIMEOUT_SEC", sessionTimeoutSeconds);
        session.setAttribute("SESSION_EXPIRE_AT", expiresAt);
        session.setAttribute("ADMIN_LOGIN", true);

        // 로그인 성공 로그 저장 (IP/User-Agent/세션ID는 서비스 내부에서 수집)
        String userId = authentication.getName();
        logService.logLoginSuccess(userId, request);

        // 로그인 메타데이터 업데이트 (마지막 로그인/실패횟수 초기화)
        repo.findByUserId(userId).ifPresent(u -> {
            u.setLastLogin(LocalDateTime.now());
            if (u.getFailedLoginAttempts() != null && u.getFailedLoginAttempts() > 0) {
                u.setFailedLoginAttempts(0);
            }
            repo.save(u);
        });

        // 기본 동작(리다이렉트)
        super.onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * SessionRegistry를 사용하여 같은 username의 '다른' 세션들을 즉시 만료시킨다.
     * expireNow()는 해당 세션이 다음 요청을 보낼 때 만료로 처리되지만,
     * 우리 프런트는 주기적으로 세션 API를 호출하므로 곧바로 로그인 페이지로 유도 가능.
     */
    private void expireOtherSessions(String username, String currentSessionId) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            String principalName = extractUsername(principal);
            if (!username.equals(principalName)) continue;

            List<SessionInformation> infos = sessionRegistry.getAllSessions(principal, false);
            for (SessionInformation info : infos) {
                if (!info.getSessionId().equals(currentSessionId)) {
                    info.expireNow(); // 즉시 만료 플래그
                }
            }
        }
    }

    /** principal 객체에서 username을 추출 */
    private String extractUsername(Object principal) {
        if (principal instanceof UserDetails ud) return ud.getUsername();
        if (principal instanceof Authentication auth) return auth.getName();
        return String.valueOf(principal);
    }
}
