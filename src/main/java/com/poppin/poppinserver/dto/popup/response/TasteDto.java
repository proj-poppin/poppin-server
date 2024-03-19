package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
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
        public static TasteDto fromEntity(TasteDto tasteDto){
                return TasteDto.builder()
                        .id(tasteDto.id)
                        .fasionBeauty(tasteDto.fasionBeauty)
                        .character(tasteDto.character)
                        .foodBeverage(tasteDto.foodBeverage)
                        .webtoonAni(tasteDto.webtoonAni)
                        .interiorThings(tasteDto.interiorThings)
                        .movie(tasteDto.movie)
                        .musical(tasteDto.musical)
                        .sports(tasteDto.sports)
                        .game(tasteDto.game)
                        .itTech(tasteDto.itTech)
                        .kpop(tasteDto.kpop)
                        .alchol(tasteDto.alchol)
                        .animalPlant(tasteDto.animalPlant)
                        .build();
        }
}
