package com.wino.wino_api.security.jwt;

import com.wino.wino_api.security.AES256Util;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * ✅ JWT 유틸리티 클래스
 * - 토큰 생성, 검증, Claim 추출
 * - email은 AES256으로 암호화하여 저장 및 복호화
 */
@Component
public class JwtUtil {

    // application.properties 또는 application.yml에서 주입받는 JWT 비밀키
    @Value("${jwt.secret}")
    private String secret;

    // 토큰 만료 시간 (예: 3600000 = 1시간)
    @Value("${jwt.expiration-ms}")
    private long expiration;

    // JWT 서명을 위한 Key 객체
    private Key secretKey;

    // 초기화 시 secretKey 생성
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 🔐 JWT 토큰 생성
     * @param encryptedEmail AES256으로 암호화된 이메일
     * @param userId 사용자 ID
     * @return JWT 토큰 문자열
     */
    public String createToken(String encryptedEmail, Long userId) {
        Claims claims = Jwts.claims();
        claims.put("email", encryptedEmail); // 암호화된 이메일을 claim에 저장
        claims.put("userId", userId);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)          // 발급 시간
                .setExpiration(expiry)     // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명
                .compact();
    }

    /**
     * 🧾 JWT에서 Claims 전체 추출
     * @param token JWT 토큰
     * @return Claims 객체
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 🔓 복호화된 사용자 이메일 반환
     * @param token JWT 토큰
     * @return 복호화된 이메일
     */
    public String getUsername(String token) {
        try {
            Claims claims = getClaims(token);
            String encryptedEmail = claims.get("email", String.class);
            return AES256Util.decrypt(encryptedEmail);
        } catch (Exception e) {
            throw new RuntimeException("JWT에서 이메일 복호화 실패", e);
        }
    }

    /**
     * ✅ 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}