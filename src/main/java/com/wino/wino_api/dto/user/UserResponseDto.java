package com.wino.wino_api.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private String userId;
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
}