package com.wino.wino_api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 에러 응답 DTO
 */
@Getter
@Builder
public class ErrorResponseDto {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
}