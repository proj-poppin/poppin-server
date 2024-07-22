package com.poppin.poppinserver.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record ApplyTokenRequestDto(
        @NotNull
        String fcmToken,

        @NotNull
        String device,

        @NotNull
        String deviceId
) {
}
