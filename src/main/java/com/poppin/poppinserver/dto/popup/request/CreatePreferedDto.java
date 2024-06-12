package com.poppin.poppinserver.dto.popup.request;

import jakarta.validation.constraints.NotNull;

public record CreatePreferedDto(
        @NotNull
        Boolean market,
        @NotNull
        Boolean display,
        @NotNull
        Boolean experience,

        Boolean wantFree
) {
}
