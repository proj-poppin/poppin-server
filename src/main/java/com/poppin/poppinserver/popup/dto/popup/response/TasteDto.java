package com.poppin.poppinserver.popup.dto.popup.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppin.poppinserver.popup.domain.TastePopup;
import lombok.Builder;

@Builder
public record TasteDto(
        Long id,
        Boolean fashionBeauty,  // 패션/뷰티
        Boolean characters, // 캐릭터
        Boolean foodBeverage,   // 음식/음료
        @JsonProperty("webtoonAnimation") Boolean webtoonAni,   // 웹툰/애니메이션
        Boolean interiorThings, // 인테리어
        Boolean movie,  // 영화
        Boolean musical,    // 뮤지컬
        Boolean sports, // 스포츠
        Boolean game,   // 게임
        Boolean itTech, // IT/기술
        Boolean kpop,   // K-POP
        Boolean alcohol,    // 주류
        Boolean animalPlant,    // 동물/식물
        Boolean etc) {
    public static TasteDto fromEntity(TastePopup tastePopup) {
        return TasteDto.builder()
                .id(tastePopup.getId())
                .fashionBeauty(tastePopup.getFashionBeauty())
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
                .alcohol(tastePopup.getAlcohol())
                .animalPlant(tastePopup.getAnimalPlant())
                .etc(tastePopup.getEtc())
                .build();
    }
}
