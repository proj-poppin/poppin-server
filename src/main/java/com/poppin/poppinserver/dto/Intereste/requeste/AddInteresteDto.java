package com.poppin.poppinserver.dto.Intereste.requeste;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AddInteresteDto(
        @NotNull
        Long popupId
) {
}
