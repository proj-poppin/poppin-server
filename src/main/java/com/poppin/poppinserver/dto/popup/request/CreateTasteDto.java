package com.poppin.poppinserver.dto.popup.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreateTasteDto(
        @NotNull
        Boolean fasionBeauty,
        @NotNull
        Boolean character,
        @NotNull
        Boolean foodBeverage,
        @NotNull
        Boolean webtoonAni,
        @NotNull
        Boolean interiorThings,
        @NotNull
        Boolean movie,
        @NotNull
        Boolean musical,
        @NotNull
        Boolean sports,
        @NotNull
        Boolean game,
        @NotNull
        Boolean itTech,
        @NotNull
        Boolean kpop,
        @NotNull
        Boolean alchol,
        @NotNull
        Boolean animalPlant) {
}
