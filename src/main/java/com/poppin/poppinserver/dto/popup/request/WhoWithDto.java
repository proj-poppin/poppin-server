package com.poppin.poppinserver.dto.popup.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WhoWithDto(
        @NotNull
        Boolean solo,
        @NotNull
        Boolean withFriend,
        @NotNull
        Boolean withFamily,
        @NotNull
        Boolean withBool) {
}
