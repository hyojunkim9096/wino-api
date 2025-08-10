package com.wino.wino_api.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    /**
     * 현재 감시자(auditor)로 사용할 값을 리턴합니다.
     * 스프링 시큐리티를 사용 중이라면, SecurityContextHolder에서
     * 인증된 사용자 이름(username)을 가져오도록 여기를 수정하세요.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: 실제 인증 사용자 정보를 리턴하도록 변경
        return Optional.of("system");
    }
}