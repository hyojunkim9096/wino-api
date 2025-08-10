package com.wino.wino_api.entity.admin;

import com.wino.wino_api.config.CryptoConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_user_info")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AdminUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;

    // 암호화 적용: 전화번호
    @Convert(converter = CryptoConverter.class)
    @Column(name = "phone_number", length = 255, nullable = false)
    private String phoneNumber;

    // 암호화 적용: 비상연락처
    @Convert(converter = CryptoConverter.class)
    @Column(name = "emergency_contact", length = 255)
    private String emergencyContact;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(length = 255)
    private String address;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "employee_type", length = 50, nullable = false)
    private String employeeType;

    @Column(length = 50, nullable = false)
    private String role;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(name = "work_location", length = 100)
    private String workLocation;

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "password_changed_date")
    private LocalDateTime passwordChangedDate;

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;
}
