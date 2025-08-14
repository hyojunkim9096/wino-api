// src/main/java/com/wino/wino_api/security/handler/AdminLogoutSuccessHandler.java
package com.wino.wino_api.security.handler;


import com.wino.wino_api.security.audit.AdminAuthLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 로그아웃 완료 시 후처리 핸들러
 * - 성공 로그 기록
 * - AJAX(fetch) 요청이면 JSON 응답
 * - 일반 요청이면 로그인 페이지로 리다이렉트
 *
 * 참고: SecurityConfig에서 invalidateHttpSession(true), deleteCookies("JSESSIONID")가 설정되어 있어
 * 세션 무효화/쿠키 삭제는 Security가 먼저 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class AdminLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AdminAuthLogService logService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {

        // 1) 로그 기록
        String userId = (authentication != null) ? authentication.getName() : "(unknown)";
        logService.logLogout(userId, request);

        // 2) AJAX 여부 판단 (fetch/XMLHttpRequest 또는 Accept: application/json)
        boolean isAjax =
                "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) ||
                        (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        // 3) 응답 처리
        if (isAjax) {
            // fetch는 302를 따라가도 페이지 네비게이션을 하지 않으므로 JSON 신호를 준다
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("X-Logged-Out", "true");
            response.getWriter().write("{\"logout\":true}");
        } else {
            // 일반 폼/링크 로그아웃은 로그인 페이지로 안내
            response.sendRedirect("/admin/login?logout");
        }
    }
}
