package com.poppin.poppinserver.dto.interest.request;

import jakarta.validation.constraints.NotNull;

public record AddInterestDto(
        @NotNull
        Long popupId,

        @NotNull
        String fcmToken
) {
}
