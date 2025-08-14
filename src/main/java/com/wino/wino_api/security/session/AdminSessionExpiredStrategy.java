package com.wino.wino_api.security.session;

import com.wino.wino_api.security.audit.AdminAuthLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AdminSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    private final AdminAuthLogService logService;

    private static final int USER_ID_MAX = 50;

    private String extractUsername(Object principal) {
        if (principal instanceof UserDetails ud) return ud.getUsername();
        if (principal instanceof Authentication a) return a.getName();
        return String.valueOf(principal);
    }

    private String cut(String s, int max) {
        if (s == null || s.isBlank()) return "(unknown)";
        return s.length() <= max ? s : s.substring(0, max);
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
            throws IOException, ServletException {

        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        // 안전하게 userId 산출
        String userId = "(unknown)";
        SessionInformation info = event.getSessionInformation();
        if (info != null) {
            userId = cut(extractUsername(info.getPrincipal()), USER_ID_MAX);
        }

        // 로깅 중 예외가 떠도 흐름을 깨지 않도록 보호
        try {
            logService.logLoginFailure(userId, "EXPIRED_BY_CONCURRENT_LOGIN", request);
        } catch (Exception ignore) {}

        // fetch/AJAX 여부에 따라 응답 분기
        boolean isAjax =
                "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) ||
                        (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        if (isAjax) {
            response.setStatus(440); // Login Time-out (비표준), 401/403 써도 됨
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("X-Session-Expired", "concurrent");
            response.getWriter().write("{\"expired\":\"concurrent\"}");
        } else {
            response.sendRedirect("/admin/login?expired=concurrent");
        }
    }
}
