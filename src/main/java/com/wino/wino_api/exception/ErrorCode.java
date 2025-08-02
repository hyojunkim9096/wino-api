package com.wino.wino_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 */
@Getter
public enum ErrorCode {
    AUTH_USER_NOT_FOUND("존재하지 않는 사용자입니다.", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ERROR("인증 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}