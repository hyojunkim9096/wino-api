package com.wino.wino_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminSignupDto {
    // === 기본 정보 ===
    private String userId;
    private String password;
    private String confirmPassword;       // 비밀번호 확인용
    private String userName;
    private String phoneNumber;           // 예: "01090964009"
    private String emergencyContact;      // 비상연락처
    private String email;
    private String postalCode;            // 우편번호
    private String address;               // 주소
    private String detailAddress;         // 상세주소
    private String employeeType;          // STAFF 또는 TEACHER
    private String role;                  // 권한 (예: ROLE_ADMIN, ROLE_TEACHER)
    private String status;                // 계정 상태 (예: ACTIVE, SUSPENDED)
    private String workLocation;          // 근무 위치 (예: 나루관, 석우관, 청계관 등)

    // === 감사(audit) 필드 ===
    private LocalDateTime createDate;     // 생성 시각 (백엔드 자동 세팅)
    private LocalDateTime updateDate;     // 최종 수정 시각 (백엔드 자동 세팅)
    private String updatedBy;             // 수정자 ID (optional)

    // === 보안·관리 확장 ===
    private LocalDateTime lastLogin;            // 마지막 로그인 시각 (optional)
    private LocalDateTime passwordChangedDate;  // 마지막 비밀번호 변경 일시 (optional)
    private Integer failedLoginAttempts;        // 연속 로그인 실패 횟수 (optional)
    private LocalDateTime accountLockedUntil;   // 로그인 잠금 해제 시각 (optional)
}
