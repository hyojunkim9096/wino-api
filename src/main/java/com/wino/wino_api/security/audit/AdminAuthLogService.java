package com.wino.wino_api.security.audit;

import com.wino.wino_api.entity.audit.AdminAuthLog;
import com.wino.wino_api.repository.audit.AdminAuthLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAuthLogService {

    private final AdminAuthLogRepository repo;

    // 스키마 최대 길이와 일치시켜 주세요 (DDL과 동일)
    private static final int MAX_USER_ID = 50;
    private static final int MAX_REASON  = 255;
    private static final int MAX_IP      = 45;
    private static final int MAX_UA      = 255;
    private static final int MAX_SESSION = 100;

    private String safe(String s, int max, String def) {
        if (s == null || s.isBlank()) return def;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String[] headerCandidates = {
                "X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP",
                "X-Forwarded", "Forwarded-For", "Forwarded"
        };
        for (String h : headerCandidates) {
            String v = request.getHeader(h);
            if (v != null && !v.isBlank()) {
                return v.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    @Transactional
    public void logLoginSuccess(String userId, HttpServletRequest req) {
        repo.save(AdminAuthLog.builder()
                .userId( safe(userId, MAX_USER_ID, "(unknown)") )
                .eventType("LOGIN_SUCCESS")
                .success(true)
                .reason(null)
                .ipAddress( safe(resolveClientIp(req), MAX_IP, "0.0.0.0") )
                .userAgent( safe(req.getHeader("User-Agent"), MAX_UA, "") )
                .sessionId( safe(req.getSession(false)!=null ? req.getSession(false).getId() : null, MAX_SESSION, null) )
                .build());
    }

    @Transactional
    public void logLoginFailure(String userIdOrInput, String reason, HttpServletRequest req) {
        repo.save(AdminAuthLog.builder()
                .userId( safe(userIdOrInput, MAX_USER_ID, "(unknown)") )
                .eventType("LOGIN_FAILURE")
                .success(false)
                .reason( safe(reason, MAX_REASON, null) )
                .ipAddress( safe(resolveClientIp(req), MAX_IP, "0.0.0.0") )
                .userAgent( safe(req.getHeader("User-Agent"), MAX_UA, "") )
                .sessionId(null)
                .build());
    }

    @Transactional
    public void logLogout(String userId, HttpServletRequest req) {
        repo.save(AdminAuthLog.builder()
                .userId( safe(userId, MAX_USER_ID, "(unknown)") )
                .eventType("LOGOUT")
                .success(true)
                .reason(null)
                .ipAddress( safe(resolveClientIp(req), MAX_IP, "0.0.0.0") )
                .userAgent( safe(req.getHeader("User-Agent"), MAX_UA, "") )
                .sessionId( safe(req.getSession(false)!=null ? req.getSession(false).getId() : null, MAX_SESSION, null) )
                .build());
    }
}
