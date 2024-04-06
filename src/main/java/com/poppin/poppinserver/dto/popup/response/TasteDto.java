package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.TastePopup;
import lombok.Builder;

@Builder
public record TasteDto(
        Long id,
        Boolean fasionBeauty,
        Boolean characters,
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
                        .fasionBeauty(tastePopup.getFashionBeauty())
                        .characters(tastePopup.getCharacters())
                        .foodBeverage(tastePopup.getFoodBeverage())
                        .webtoonAni(tastePopup.getWebtoonAni())
                        .interiorThings(tastePopup.getInteriorThings())
                        .movie(tastePopup.getMovie())
                        .musical(tastePopup.getMusical())
                        .sports(tastePopup.getSports())
                        .game(tastePopup.getGame())
                        .itTech(tastePopup.getItTech())
                        .kpop(tastePopup.getKpop())
                        .alchol(tastePopup.getAlcohol())
                        .animalPlant(tastePopup.getAnimalPlant())
                        .build();
        }
}
