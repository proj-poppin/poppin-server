package com.poppin.poppinserver.dto.fcm.request;

import jakarta.validation.constraints.NotNull;

public record TokenRequestDto(
        @NotNull
        String token,

        @NotNull
        String device
) {
}
