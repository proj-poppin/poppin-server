package com.poppin.poppinserver.dto.notification.request;

import jakarta.validation.constraints.NotNull;

public record FCMRequestDto(

        @NotNull
        Long userId,

        @NotNull
        Long popupId,

        @NotNull
        String token,

        @NotNull
        String title,

        @NotNull
        String body,

        String image
) {
}
