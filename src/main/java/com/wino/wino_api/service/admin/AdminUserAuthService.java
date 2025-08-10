// src/main/java/com/wino/wino_api/service/admin/AdminUserAuthService.java
package com.wino.wino_api.service.admin;

import com.wino.wino_api.dto.admin.AdminSignupDto;
import com.wino.wino_api.entity.admin.AdminUserInfo;
import com.wino.wino_api.repository.admin.AdminUserInfoRepository;
import com.wino.wino_api.security.AES256Util;
import com.wino.wino_api.security.admin.AdminUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserAuthService implements UserDetailsService {

    private final AdminUserInfoRepository repository;
    private final PasswordEncoder passwordEncoder;

    // 로그인용
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        AdminUserInfo admin = repository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("관리자 계정을 찾을 수 없습니다."));
        return new AdminUserDetails(admin); // ROLE_ADMIN 포함
    }

    // 회원가입 (기존 코드 유지)
    @Transactional
    public AdminUserInfo register(AdminSignupDto dto, String currentUserId) {
        if (repository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 userId입니다.");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 확인용 비밀번호가 일치하지 않습니다.");
        }

        AdminUserInfo adminUser = AdminUserInfo.builder()
                .userId(dto.getUserId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .userName(dto.getUserName())
                .phoneNumber(dto.getPhoneNumber())
                .emergencyContact(dto.getEmergencyContact())
                .email(dto.getEmail())
                .postalCode(dto.getPostalCode())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .employeeType(dto.getEmployeeType())
                .status(dto.getStatus())
                .failedLoginAttempts(0)
                .build();

        adminUser.setUpdatedBy(currentUserId);
        adminUser.setStatus("TEMPORARY");
        return repository.save(adminUser);
    }

    // 평문 전화번호 조회 (기존 코드 유지)
    @Transactional(readOnly = true)
    public AdminUserInfo findByPhoneNumber(String plainPhone) {
        try {
            String encrypted = AES256Util.encrypt(plainPhone);
            return repository.findByPhoneNumber(encrypted)
                    .orElseThrow(() -> new RuntimeException("해당 번호의 관리자를 찾을 수 없습니다."));
        } catch (Exception e) {
            throw new RuntimeException("전화번호 암호화 중 오류 발생", e);
        }
    }
}
