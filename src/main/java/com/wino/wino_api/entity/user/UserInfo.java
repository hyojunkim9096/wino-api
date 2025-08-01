package com.wino.wino_api.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 정보 엔티티
 * user_info 테이블과 매핑되며 이메일, 비밀번호, 이름, 전화번호 등 기본 정보 저장
 */
@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    /**
     * 사용자 고유 ID (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 로그인용 이메일 (중복 불가)
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 암호화된 비밀번호
     */
    @Column(nullable = false)
    private String password;

    /**
     * 사용자 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 휴대폰 번호
     */
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    /**
     * 가입일시 (자동 생성)
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시 (자동 갱신)
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}