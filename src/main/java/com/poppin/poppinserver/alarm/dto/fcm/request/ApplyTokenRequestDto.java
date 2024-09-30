package com.poppin.poppinserver.alarm.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record ApplyTokenRequestDto(
        @NotNull
        String fcmToken,

        @NotNull
        Long userId
) {
}
