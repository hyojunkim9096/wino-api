// src/main/java/com/wino/wino_api/security/admin/AdminUserDetails.java
package com.wino.wino_api.security.admin;

import com.wino.wino_api.entity.admin.AdminUserInfo;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
public class AdminUserDetails implements UserDetails {

    private final String userId;
    private final String password;
    private final String status;                 // ACTIVE / TEMPORARY / LOCKED / INACTIVE
    private final Integer failedLoginAttempts;
    private final LocalDateTime accountLockedUntil;
    private static final int MAX_FAILED = 5;

    public AdminUserDetails(AdminUserInfo u) {
        this.userId = u.getUserId();
        this.password = u.getPassword();
        this.status = u.getStatus();
        this.failedLoginAttempts = u.getFailedLoginAttempts();
        this.accountLockedUntil = u.getAccountLockedUntil();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Override
    public String getUsername() { return userId; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        if ("LOCKED".equalsIgnoreCase(status)) return false;
        if (accountLockedUntil != null && LocalDateTime.now().isBefore(accountLockedUntil)) return false;
        Integer fail = failedLoginAttempts;
        return fail == null || fail < MAX_FAILED;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        // ACTIVE만 허용 → TEMPORARY/INACTIVE는 DisabledException 발생 → FailureHandler가 메시지 분기
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
