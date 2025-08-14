// src/main/java/com/wino/wino_api/controller/admin/api/SessionController.java
package com.wino.wino_api.controller.admin.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 관리자 세션 상태 조회/연장용 API
 * - SPA/페이지 내 활동(클릭/키보드 등) 시 호출하여 세션을 연장(슬라이딩)하고
 *   새로운 만료시각을 프런트로 내려준다.
 * - 응답 키: now, maxInactiveIntervalSec, expiresAt(밀리초), remainingSec(초)
 */
@RestController
@RequestMapping("/admin/api/session")
public class SessionController {

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info(HttpSession session) {
        int maxSec = session.getMaxInactiveInterval();
        long now = System.currentTimeMillis();
        long expiresAt = now + (maxSec * 1000L);
        int remainingSec = (int) Math.max(0, (expiresAt - now) / 1000L);

        Map<String, Object> body = new HashMap<>();
        body.put("now", now);
        body.put("maxInactiveIntervalSec", maxSec);
        body.put("expiresAt", expiresAt);
        body.put("remainingSec", remainingSec); // ← 추가: js가 바로 읽어 사용 가능
        return ResponseEntity.ok(body);
    }

    @PostMapping("/extend")
    public ResponseEntity<Map<String, Object>> extend(HttpSession session) {
        // 이 요청 자체가 lastAccessedTime 갱신 효과를 가지므로 별도 작업 불필요
        session.setAttribute("lastExtendAt", System.currentTimeMillis());

        int maxSec = session.getMaxInactiveInterval();
        long now = System.currentTimeMillis();
        long expiresAt = now + (maxSec * 1000L);
        int remainingSec = (int) Math.max(0, (expiresAt - now) / 1000L);

        Map<String, Object> body = new HashMap<>();
        body.put("now", now);
        body.put("maxInactiveIntervalSec", maxSec);
        body.put("expiresAt", expiresAt);
        body.put("remainingSec", remainingSec); // ← 추가
        return ResponseEntity.ok(body);
    }
}
