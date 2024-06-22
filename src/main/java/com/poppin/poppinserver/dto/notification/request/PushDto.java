package com.poppin.poppinserver.dto.notification.request;

import jakarta.validation.constraints.NotNull;


public record PushDto(
        @NotNull
        String token,

        @NotNull
        String title,

        @NotNull
        String body,

        @NotNull
        Long popupId

) {
}
