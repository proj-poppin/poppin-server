package com.poppin.poppinserver.dto.notification.request;

import jakarta.validation.constraints.NotNull;

public record PushDto(
        @NotNull
        String token,

        @NotNull
        String title,

        String body,

        String image

) {
}
