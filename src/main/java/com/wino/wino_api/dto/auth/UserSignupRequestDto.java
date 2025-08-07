package com.wino.wino_api.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 회원가입 요청 DTO
 */
@Getter
@Setter
@ToString
public class UserSignupRequestDto {

    private String userId;       // 로그인용 ID
    private String password;     // 비밀번호
    private String name;         // 실명
    private String nickname;     // 닉네임 (중복 불가)
    private String email;        // 선택 입력
    private String phoneNumber;  // 전화번호
    private boolean agreeToPrivacyPolicy; // 개인정보 수집 동의
}