package com.poppin.poppinserver.user.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AuthLoginRequestDto(
        // 이메일 형식
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "password is required.")
        String password,

        // FCM 토큰 형식 검증
        @NotNull(message = "fcmToken is required.")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-_:]{120,}$",
                message = "올바른 FCM 토큰 형식이 아닙니다."
        )
        String fcmToken
) {
}
