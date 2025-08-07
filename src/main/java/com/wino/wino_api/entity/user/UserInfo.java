package com.wino.wino_api.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 정보 엔티티
 * user_info 테이블과 매핑되며 로그인 ID, 이름, 닉네임, 연락처, 이메일, 동의 여부 등을 포함
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
     * 로그인용 ID (user_id, 중복 불가)
     */
    @Column(name = "user_id", nullable = false, unique = true, length = 30)
    private String userId;

    /**
     * 암호화된 비밀번호
     */
    @Column(nullable = false)
    private String password;

    /**
     * 사용자 실명
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 닉네임 (중복 불가)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    /**
     * 이메일 (선택사항, 중복 불가)
     */
    @Column(unique = true, length = 100)
    private String email;

    /**
     * 휴대폰 번호
     */
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    /**
     * 개인정보 수집 및 이용 동의 여부
     */
    @Column(name = "agree_to_privacy_policy", nullable = false)
    private boolean agreeToPrivacyPolicy;

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