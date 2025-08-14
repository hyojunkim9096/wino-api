package com.wino.wino_api.security.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 사용자ID -> 활성 HttpSession 매핑.
 * - 새 로그인 시 기존 세션이 있으면 즉시 invalidate()하여 끊는다.
 * - 단일 서버 기준. 멀티 서버는 Spring Session(예: Redis) 사용 권장.
 */
@Component
public class AdminSessionStore {

    private final Map<String, HttpSession> map = new ConcurrentHashMap<>();

    /** 동일 사용자 이전 세션을 즉시 끊기 */
    public void invalidateOther(String userId, String currentSessionId) {
        HttpSession old = map.get(userId);
        if (old != null) {
            try {
                if (old.getId() != null && !old.getId().equals(currentSessionId)) {
                    old.invalidate(); // 즉시 끊기
                }
            } catch (IllegalStateException ignore) {
                // 이미 만료된 세션이면 무시
            }
        }
    }

    /** 현재 세션을 등록(마지막 로그인 세션으로 간주) */
    public void register(String userId, HttpSession session) {
        map.put(userId, session);
    }

    /** 세션 소멸 시 정리용 */
    public void removeBySession(HttpSession session) {
        if (session == null) return;
        map.entrySet().removeIf(e -> e.getValue() == session);
    }

    /** 로그아웃 시 정리 */
    public void removeByUser(String userId) {
        map.remove(userId);
    }
}
