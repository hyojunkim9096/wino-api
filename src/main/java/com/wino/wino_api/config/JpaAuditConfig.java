package com.wino.wino_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
    // JPA Auditing 활성화용 설정 클래스입니다.
    // AuditorAware 구현체가 @Component로 등록되어 있으면, 자동으로 사용됩니다.
}
