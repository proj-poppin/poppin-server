package com.poppin.poppinserver.user.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record AppleUserIdRequestDto(
        @NotBlank String appleUserId,
        String fcmToken
) {
}
