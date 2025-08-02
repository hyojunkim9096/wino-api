package com.wino.wino_api.controller.auth;

import com.wino.wino_api.dto.auth.UserLoginRequestDto;
import com.wino.wino_api.dto.auth.UserSignupRequestDto;
import com.wino.wino_api.service.user.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러 (회원가입, 로그인)
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;

    /**
     * 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto request) throws Exception {
        log.info("회원가입 요청 : {}", request);
        userAuthService.register(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    /**
     * 로그인 API
     */
    @PostMapping("/loginCheck")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto request) throws Exception {
        log.info("로그인 요청 : {}", request);
        String token = userAuthService.login(request); // JWT 발급 or 로그인 검증
        return ResponseEntity.ok(token);
    }
}
