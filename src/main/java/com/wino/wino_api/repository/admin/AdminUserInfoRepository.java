package com.wino.wino_api.repository.admin;

import com.wino.wino_api.entity.admin.AdminUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserInfoRepository extends JpaRepository<AdminUserInfo, Long> {

    // 중복 체크용
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);

    // (기존) 암호화된 전화번호로 조회 — 로그인에는 사용 안 함
    Optional<AdminUserInfo> findByPhoneNumber(String encryptedPhone);

    // ★ 로그인용: userId로 관리자 1명 찾기
    Optional<AdminUserInfo> findByUserId(String userId);
}
