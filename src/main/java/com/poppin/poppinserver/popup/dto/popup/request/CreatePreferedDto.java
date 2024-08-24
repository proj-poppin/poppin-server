package com.poppin.poppinserver.popup.dto.popup.request;

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
