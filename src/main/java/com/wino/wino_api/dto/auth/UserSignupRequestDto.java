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
    private String email;
    private String password;
    private String name;
    private String phoneNumber;

}
