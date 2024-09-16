package com.poppin.poppinserver.user.dto.auth.request;

import jakarta.validation.constraints.NotNull;

public record FcmTokenRequestDto(
        @NotNull(message = "FCM Token is required")
        String fcmToken
) {
}
