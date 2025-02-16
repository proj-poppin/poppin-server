package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.repository.WhoWithPopupRepository;
import com.poppin.poppinserver.popup.service.BootstrapService;
import com.poppin.poppinserver.popup.service.PopupService;
import com.poppin.poppinserver.popup.usecase.PreferedPopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.TastedPopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.WhoWithPopupCommandUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceSettingDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceUpdateResponseDto;
import com.poppin.poppinserver.user.repository.UserCommandRepository;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferenceSettingService {
    private final UserQueryUseCase userQueryUseCase;
    private final UserCommandRepository userCommandRepository;
    private final BootstrapService bootstrapService;
    private final PopupService popupService;
    private final WhoWithPopupRepository whoWithPopupRepository;

    private final PreferedPopupCommandUseCase preferedPopupCommandUseCase;
    private final TastedPopupCommandUseCase tastedPopupCommandUseCase;
    private final WhoWithPopupCommandUseCase whoWithPopupCommandUseCase;

    public UserPreferenceSettingDto readUserPreference(Long userId) {
        User user = userQueryUseCase.findUserById(userId);
        createDefaultUserTaste(user);
        return UserPreferenceSettingDto.fromEntity(user.getPreferedPopup(), user.getTastePopup(),
                user.getWhoWithPopup());
    }

    public void createDefaultUserTaste(User user) {
        if (user.getPreferedPopup() == null) {
            PreferedPopup preferedPopup = createDefaultPreferedPopup();
            preferedPopupCommandUseCase.createPreferedPopup(preferedPopup);
            user.updatePopupTaste(preferedPopup);
        }

        if (user.getTastePopup() == null) {
            TastePopup tastePopup = createDefaultTastePopup();
            tastedPopupCommandUseCase.createTastePopup(tastePopup);
            user.updatePopupTaste(tastePopup);
        }

        if (user.getWhoWithPopup() == null) {
            WhoWithPopup whoWithPopup = createDefaultWhoWithPopup();
            whoWithPopupRepository.save(whoWithPopup);
            user.updatePopupTaste(whoWithPopup);
        }

        userCommandRepository.save(user);
    }

    @Transactional
    public UserPreferenceUpdateResponseDto updateUserPreference(Long userId, CreateUserTasteDto createUserTasteDto) {
        User user = userQueryUseCase.findUserById(userId);

        if (!readUserPreferenceSettingCreated(user.getId())) {
            createDefaultUserTaste(user);
        }

        PreferedPopup preferedPopup = user.getPreferedPopup();
        preferedPopupCommandUseCase.updatePreferedPopup(preferedPopup, createUserTasteDto.preference());

        TastePopup tastePopup = user.getTastePopup();
        tastedPopupCommandUseCase.updateTastePopup(tastePopup, createUserTasteDto.taste());

        WhoWithPopup whoWithPopup = user.getWhoWithPopup();
        whoWithPopup.update(createUserTasteDto.whoWith().solo(),
                createUserTasteDto.whoWith().withFriend(),
                createUserTasteDto.whoWith().withFamily(),
                createUserTasteDto.whoWith().withLover());
        whoWithPopupRepository.save(whoWithPopup);

        user.updatePopupTaste(preferedPopup, tastePopup, whoWithPopup);
        userCommandRepository.save(user);

        UserPreferenceSettingDto userPreferenceSettingDto = UserPreferenceSettingDto
                .fromEntity(
                        preferedPopup, tastePopup, whoWithPopup
                );

        // 취향 저격 팝업 조회
        List<Popup> recommendPopup = bootstrapService.getRecommendPopup(userId);
        List<PopupStoreDto> recommendedPopupStores = popupService.getPopupStoreDtos(recommendPopup, userId);

        return UserPreferenceUpdateResponseDto.fromDtos(userPreferenceSettingDto, recommendedPopupStores);
    }

    @Transactional
    public boolean readUserPreferenceSettingCreated(Long userId) {
        User user = userQueryUseCase.findUserById(userId);

        createDefaultUserTaste(user);
        boolean hasPreferences = true;

        PreferedPopup preferedPopup = user.getPreferedPopup();
        TastePopup tastePopup = user.getTastePopup();
        WhoWithPopup whoWithPopup = user.getWhoWithPopup();

        if (!preferedPopup.getDisplay() && !preferedPopup.getExperience() &&
                !preferedPopup.getMarket() && !preferedPopup.getWantFree() &&
                !tastePopup.getAlcohol() && !tastePopup.getAnimalPlant() &&
                !tastePopup.getCharacters() && !tastePopup.getFashionBeauty() &&
                !tastePopup.getFoodBeverage() && !tastePopup.getGame() &&
                !tastePopup.getInteriorThings() && !tastePopup.getItTech() &&
                !tastePopup.getKpop() && !tastePopup.getMovie() &&
                !tastePopup.getMusical() && !tastePopup.getSports() &&
                !tastePopup.getWebtoonAni() &&
                !whoWithPopup.getSolo() && !whoWithPopup.getWithFamily() &&
                !whoWithPopup.getWithFriend() && !whoWithPopup.getWithLover()
        ) {
            hasPreferences = false;
        }

        return hasPreferences;
    }

    private PreferedPopup createDefaultPreferedPopup() {
        return PreferedPopup.builder()
                .market(false)
                .display(false)
                .experience(false)
                .wantFree(false)
                .build();
    }

    private TastePopup createDefaultTastePopup() {
        return TastePopup.builder()
                .fasionBeauty(false)
                .characters(false)
                .foodBeverage(false)
                .webtoonAni(false)
                .interiorThings(false)
                .movie(false)
                .musical(false)
                .sports(false)
                .game(false)
                .itTech(false)
                .kpop(false)
                .alcohol(false)
                .animalPlant(false)
                .etc(false)
                .build();
    }

    private WhoWithPopup createDefaultWhoWithPopup() {
        return WhoWithPopup.builder()
                .solo(false)
                .withFriend(false)
                .withFamily(false)
                .withLover(false)
                .build();
    }
}
