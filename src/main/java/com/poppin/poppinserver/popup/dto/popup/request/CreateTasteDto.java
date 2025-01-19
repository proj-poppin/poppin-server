package com.poppin.poppinserver.popup.dto.popup.request;

import jakarta.validation.constraints.NotNull;

public record CreateTasteDto(
        @NotNull
        Boolean fashionBeauty,
        @NotNull
        Boolean characters,
        @NotNull
        Boolean foodBeverage,
        @NotNull
        Boolean webtoonAnimation,
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
        Boolean alcohol,
        @NotNull
        Boolean animalPlant,
        Boolean etc) {
}
