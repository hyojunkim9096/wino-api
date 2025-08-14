package com.wino.wino_api.entity.audit;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 관리자 인증(로그인/실패/로그아웃) 로그 엔티티
 */
@Entity
@Table(name = "admin_auth_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AdminAuthLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 로그인 시도한 관리자 ID (성공/실패 모두 기록) */
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    /** LOGIN_SUCCESS | LOGIN_FAILURE | LOGOUT */
    @Column(name = "event_type", length = 20, nullable = false)
    private String eventType;

    /** 성공 여부 (로그아웃은 true로 처리) */
    @Column(nullable = false)
    private boolean success;

    /** 실패 사유/부가 정보 (예: BAD_CREDENTIALS, ACCOUNT_LOCKED 등) */
    @Column(length = 255)
    private String reason;

    /** 접속 IP */
    @Column(name = "ip_address", length = 45, nullable = false)
    private String ipAddress;

    /** User-Agent */
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    /** 세션 ID (성공/로그아웃 시 주로 기록) */
    @Column(name = "session_id", length = 100)
    private String sessionId;

    /** 생성 시각 */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
