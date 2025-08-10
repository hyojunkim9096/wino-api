package com.wino.wino_api.service.auth;

import com.wino.wino_api.entity.admin.AdminUserInfo;
import com.wino.wino_api.entity.auth.PasswordResetCode;
import com.wino.wino_api.repository.admin.AdminUserInfoRepository;
import com.wino.wino_api.repository.auth.PasswordResetCodeRepository;
import com.wino.wino_api.service.common.MailService;
import com.wino.wino_api.util.OtpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetByCodeService {

    private final AdminUserInfoRepository adminRepo;
    private final PasswordResetCodeRepository codeRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private static final int EXP_MIN = 10;

    @Transactional
    public Optional<String> issueCodeByUserId(String userId, String requesterIp) {
        var opt = adminRepo.findByUserId(userId);
        if (opt.isEmpty()) {
            log.info("[PW-OTP] userId not found: {}", userId);
            return Optional.empty();
        }
        AdminUserInfo user = opt.get();
        String st = user.getStatus();
        boolean allowed = "TEMPORARY".equalsIgnoreCase(st) || "ACTIVE".equalsIgnoreCase(st) || "LOCKED".equalsIgnoreCase(st);
        if (!allowed) {
            log.info("[PW-OTP] blocked by status={} userId={}", st, userId);
            return Optional.empty();
        }

        String code = OtpUtils.generate6digit();
        String hash = OtpUtils.sha256Hex(code);

        PasswordResetCode rec = PasswordResetCode.builder()
                .user(user)
                .codeHash(hash)
                .expiresAt(LocalDateTime.now().plusMinutes(EXP_MIN))
                .used(false)
                .requesterIp(requesterIp)
                .build();
        codeRepo.save(rec);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            mailService.sendPasswordResetCode(user.getEmail(), user.getUserId(), code, EXP_MIN);
        } else {
            log.warn("[PW-OTP] userId={} has empty email", user.getUserId());
        }
        return Optional.of(code);
    }

    @Transactional
    public void resetByCode(String userId, String plainCode, String newRawPassword) {
        AdminUserInfo user = adminRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        PasswordResetCode latest = codeRepo.findFirstByUserAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                user, LocalDateTime.now()
        ).orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 만료된 코드입니다."));

        if (!latest.getCodeHash().equals(OtpUtils.sha256Hex(plainCode))) {
            throw new IllegalArgumentException("코드가 올바르지 않습니다.");
        }
        if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalStateException("비활성화된 계정입니다. 관리자에게 문의하세요.");
        }

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(newRawPassword));
        user.setPasswordChangedDate(LocalDateTime.now());

        // LOCKED 해제 정책
        String before = user.getStatus();
        if ("LOCKED".equalsIgnoreCase(before)) {
            user.setStatus("ACTIVE");
            if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
                user.setFailedLoginAttempts(0);
            }
            user.setAccountLockedUntil(null); // 시간잠금 해제
        }

        latest.setUsed(true);
        latest.setUsedAt(LocalDateTime.now());
        log.info("[PW-OTP] reset done userId={} {}->{}", userId, before, user.getStatus());
    }
}
