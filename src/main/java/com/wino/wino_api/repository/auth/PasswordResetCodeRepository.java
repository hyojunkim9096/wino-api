// src/main/java/com/wino/wino_api/repository/auth/PasswordResetCodeRepository.java
package com.wino.wino_api.repository.auth;

import com.wino.wino_api.entity.auth.PasswordResetCode;
import com.wino.wino_api.entity.admin.AdminUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    /** 가장 최근 미사용 + 미만료 코드(1건) */
    Optional<PasswordResetCode> findFirstByUserAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
            AdminUserInfo user, LocalDateTime now
    );
}
