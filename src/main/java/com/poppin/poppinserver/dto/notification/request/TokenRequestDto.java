package com.poppin.poppinserver.dto.notification.request;

import jakarta.validation.constraints.NotNull;

public record TokenRequestDto(
        @NotNull
        Long userId,

        @NotNull
        String token,

        @NotNull
        String device // android or ios
) {
}
