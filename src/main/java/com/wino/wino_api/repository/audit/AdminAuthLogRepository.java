package com.wino.wino_api.repository.audit;

import com.wino.wino_api.entity.audit.AdminAuthLog;
import org.springframework.data.jpa.repository.JpaRepository;

/** 관리자 인증 로그 저장소 */
public interface AdminAuthLogRepository extends JpaRepository<AdminAuthLog, Long> {
}
