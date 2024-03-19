package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TasteDto(
        Long id,
        Boolean fasionBeauty,
        Boolean character,
        Boolean foodBeverage,
        Boolean webtoonAni,
        Boolean interiorThings,
        Boolean movie,
        Boolean musical,
        Boolean sports,
        Boolean game,
        Boolean itTech,
        Boolean kpop,
        Boolean alchol,
        Boolean animalPlant) {
}
