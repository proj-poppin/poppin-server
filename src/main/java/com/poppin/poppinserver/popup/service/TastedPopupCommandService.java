package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.repository.TastePopupRepository;
import com.poppin.poppinserver.popup.usecase.TastedPopupCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TastedPopupCommandService implements TastedPopupCommandUseCase {
    private final TastePopupRepository tastePopupRepository;
    @Override
    public TastePopup createProxyTastePopup(TastePopup tastePopup) {
        return tastePopupRepository.save(
                TastePopup.builder()
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
                        .alcohol(tastePopup.getAlcohol())
                        .animalPlant(tastePopup.getAnimalPlant())
                        .etc(tastePopup.getEtc())
                        .build()
        );
    }

    @Override
    public TastePopup createTastePopup(TastePopup tastePopup) {
        return tastePopupRepository.save(tastePopup);
    }

    @Override
    public TastePopup createTastePopup(CreateTasteDto createTasteDto) {
        return tastePopupRepository.save(
                TastePopup.builder()
                .fasionBeauty(createTasteDto.fashionBeauty())
                .characters(createTasteDto.characters())
                .foodBeverage(createTasteDto.foodBeverage())
                .webtoonAni(createTasteDto.webtoonAnimation())
                .interiorThings(createTasteDto.interiorThings())
                .movie(createTasteDto.movie())
                .musical(createTasteDto.musical())
                .sports(createTasteDto.sports())
                .game(createTasteDto.game())
                .itTech(createTasteDto.itTech())
                .kpop(createTasteDto.kpop())
                .alcohol(createTasteDto.alcohol())
                .animalPlant(createTasteDto.animalPlant())
                .etc(createTasteDto.etc() != null && createTasteDto.etc())
                .build()
        );
    }

    @Override
    public TastePopup createTastePopup(List<String> taste) {
        return tastePopupRepository.save(
                TastePopup.builder()
                .fasionBeauty(taste.contains("fashionBeauty"))
                .characters(taste.contains("characters"))
                .foodBeverage(taste.contains("foodBeverage"))
                .webtoonAni(taste.contains("webtoonAni"))
                .interiorThings(taste.contains("interiorThings"))
                .movie(taste.contains("movie"))
                .musical(taste.contains("musical"))
                .sports(taste.contains("sports"))
                .game(taste.contains("game"))
                .itTech(taste.contains("itTech"))
                .kpop(taste.contains("kpop"))
                .alcohol(taste.contains("alcohol"))
                .animalPlant(taste.contains("animalPlant"))
                .etc(taste.contains("etc"))
                .build()
        );
    }

    @Override
    public void updateTastePopup(TastePopup tastePopup, CreateTasteDto createTasteDto) {
        tastePopup.update(createTasteDto.fashionBeauty(),
                createTasteDto.characters(),
                createTasteDto.foodBeverage(),
                createTasteDto.webtoonAnimation(),
                createTasteDto.interiorThings(),
                createTasteDto.movie(),
                createTasteDto.musical(),
                createTasteDto.sports(),
                createTasteDto.game(),
                createTasteDto.itTech(),
                createTasteDto.kpop(),
                createTasteDto.alcohol(),
                createTasteDto.animalPlant(),
                createTasteDto.etc());
        tastePopupRepository.save(tastePopup);
    }
}
