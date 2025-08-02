package com.wino.wino_api.dto.auth;

import lombok.Data;

/**
 * 로그인 요청 DTO
 */
@Data
public class UserLoginRequestDto {
    private String email;
    private String password;
}
