package com.wino.wino_api.security.session;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 세션 만료/무효화 시 AdminSessionStore에서 정리.
 */
@Component
@RequiredArgsConstructor
public class AdminSessionCleanupListener implements HttpSessionListener {

    private final AdminSessionStore store;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        store.removeBySession(se.getSession());
    }
}
