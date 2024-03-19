package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.TastePopup;
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
        public static TasteDto fromEntity(TastePopup tastePopup){
                return TasteDto.builder()
                        .id(tastePopup.getId())
                        .fasionBeauty(tastePopup.getFasionBeauty())
                        .character(tastePopup.getCharacter())
                        .foodBeverage(tastePopup.getFoodBeverage())
                        .webtoonAni(tastePopup.getWebtoonAni())
                        .interiorThings(tastePopup.getInteriorThings())
                        .movie(tastePopup.getMovie())
                        .musical(tastePopup.getMusical())
                        .sports(tastePopup.getSports())
                        .game(tastePopup.getGame())
                        .itTech(tastePopup.getItTech())
                        .kpop(tastePopup.getKpop())
                        .alchol(tastePopup.getAlchol())
                        .animalPlant(tastePopup.getAnimalPlant())
                        .build();
        }
}
