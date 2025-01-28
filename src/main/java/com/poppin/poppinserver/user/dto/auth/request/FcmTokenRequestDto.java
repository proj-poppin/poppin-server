package com.poppin.poppinserver.user.dto.auth.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record FcmTokenRequestDto(
        @NotNull(message = "fcmToken is required.")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-_:]{120,}$",
                message = "올바른 FCM 토큰 형식이 아닙니다."
        )
        String fcmToken
) {
}
