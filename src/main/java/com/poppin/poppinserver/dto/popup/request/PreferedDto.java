package com.poppin.poppinserver.dto.popup.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PreferedDto(
        @NotNull
        Boolean market,
        @NotNull
        Boolean display,
        @NotNull
        Boolean experience,
        @NotNull
        Boolean wantFree
) {
}
