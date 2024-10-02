package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.popup.dto.popup.response.PreferedDto;
import com.poppin.poppinserver.popup.dto.popup.response.TasteDto;
import com.poppin.poppinserver.popup.dto.popup.response.WhoWithDto;
import com.poppin.poppinserver.popup.repository.PreferedPopupRepository;
import com.poppin.poppinserver.popup.repository.TastePopupRepository;
import com.poppin.poppinserver.popup.repository.WhoWithPopupRepository;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.user.request.CreateUserTasteDto;
import com.poppin.poppinserver.user.dto.user.response.UserPreferenceSettingDto;
import com.poppin.poppinserver.user.dto.user.response.UserTasteResponseDto;
import com.poppin.poppinserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferenceSettingService {
    private final UserRepository userRepository;
    private final PreferedPopupRepository preferedPopupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final WhoWithPopupRepository whoWithPopupRepository;

    // TODO: 삭제 예정
    @Transactional
    public UserTasteResponseDto createUserTaste(
            Long userId,
            CreateUserTasteDto createUserTasteDto
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (user.getPreferedPopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        PreferedPopup preferedPopup = PreferedPopup.builder()
                .market(createUserTasteDto.preference().market())
                .display(createUserTasteDto.preference().display())
                .experience(createUserTasteDto.preference().experience())
                .wantFree(createUserTasteDto.preference().wantFree())
                .build();
        preferedPopupRepository.save(preferedPopup);

        if (user.getTastePopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createUserTasteDto.taste().fashionBeauty())
                .characters(createUserTasteDto.taste().characters())
                .foodBeverage(createUserTasteDto.taste().foodBeverage())
                .webtoonAni(createUserTasteDto.taste().webtoonAni())
                .interiorThings(createUserTasteDto.taste().interiorThings())
                .movie(createUserTasteDto.taste().movie())
                .musical(createUserTasteDto.taste().musical())
                .sports(createUserTasteDto.taste().sports())
                .game(createUserTasteDto.taste().game())
                .itTech(createUserTasteDto.taste().itTech())
                .kpop(createUserTasteDto.taste().kpop())
                .alcohol(createUserTasteDto.taste().alcohol())
                .animalPlant(createUserTasteDto.taste().animalPlant())
                .build();
        tastePopupRepository.save(tastePopup);

        if (user.getWhoWithPopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        WhoWithPopup whoWithPopup = WhoWithPopup.builder()
                .solo(createUserTasteDto.whoWith().solo())
                .withFriend(createUserTasteDto.whoWith().withFriend())
                .withFamily(createUserTasteDto.whoWith().withFamily())
                .withLover(createUserTasteDto.whoWith().withLover())
                .build();
        whoWithPopupRepository.save(whoWithPopup);

        user.updatePopupTaste(preferedPopup, tastePopup, whoWithPopup);
        userRepository.save(user);

        return UserTasteResponseDto.builder()
                .preference(PreferedDto.fromEntity(preferedPopup))
                .taste(TasteDto.fromEntity(tastePopup))
                .whoWith(WhoWithDto.fromEntity(whoWithPopup))
                .build();
    }

    // TODO: 삭제 예정
    public UserTasteResponseDto readUserTaste(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (user.getPreferedPopup() == null) {
            PreferedPopup preferedPopup = createDefaultPreferedPopup();
            preferedPopupRepository.save(preferedPopup);
            user.updatePopupTaste(preferedPopup);
        }

        if (user.getTastePopup() == null) {
            TastePopup tastePopup = createDefaultTastePopup();
            tastePopupRepository.save(tastePopup);
            user.updatePopupTaste(tastePopup);
        }

        if (user.getWhoWithPopup() == null) {
            WhoWithPopup whoWithPopup = createDefaultWhoWithPopup();
            whoWithPopupRepository.save(whoWithPopup);
            user.updatePopupTaste(whoWithPopup);
        }

        userRepository.save(user);
        return UserTasteResponseDto.builder()
                .preference(PreferedDto.fromEntity(user.getPreferedPopup()))
                .taste(TasteDto.fromEntity(user.getTastePopup()))
                .whoWith(WhoWithDto.fromEntity(user.getWhoWithPopup()))
                .build();
    }

    @Transactional
    public UserTasteResponseDto updateUserTaste(Long userId, CreateUserTasteDto createUserTasteDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        PreferedPopup preferedPopup = user.getPreferedPopup();
        preferedPopup.update(createUserTasteDto.preference().market(),
                createUserTasteDto.preference().display(),
                createUserTasteDto.preference().experience(),
                createUserTasteDto.preference().wantFree());
        preferedPopupRepository.save(preferedPopup);

        TastePopup tastePopup = user.getTastePopup();
        tastePopup.update(createUserTasteDto.taste().fashionBeauty(),
                createUserTasteDto.taste().characters(),
                createUserTasteDto.taste().foodBeverage(),
                createUserTasteDto.taste().webtoonAni(),
                createUserTasteDto.taste().interiorThings(),
                createUserTasteDto.taste().movie(),
                createUserTasteDto.taste().musical(),
                createUserTasteDto.taste().sports(),
                createUserTasteDto.taste().game(),
                createUserTasteDto.taste().itTech(),
                createUserTasteDto.taste().kpop(),
                createUserTasteDto.taste().alcohol(),
                createUserTasteDto.taste().animalPlant(),
                null);
        tastePopupRepository.save(tastePopup);

        WhoWithPopup whoWithPopup = user.getWhoWithPopup();
        whoWithPopup.update(createUserTasteDto.whoWith().solo(),
                createUserTasteDto.whoWith().withFriend(),
                createUserTasteDto.whoWith().withFamily(),
                createUserTasteDto.whoWith().withLover());
        whoWithPopupRepository.save(whoWithPopup);

        user.updatePopupTaste(preferedPopup, tastePopup, whoWithPopup);
        userRepository.save(user);

        return UserTasteResponseDto.builder()
                .preference(PreferedDto.fromEntity(preferedPopup))
                .taste(TasteDto.fromEntity(tastePopup))
                .whoWith(WhoWithDto.fromEntity(whoWithPopup))
                .build();
    }

    @Transactional
    public UserPreferenceSettingDto readUserPreferenceSettingCreated(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        UserTasteResponseDto userTasteResponseDto = readUserTaste(userId);
        boolean isPreferenceSettingCreated = updatePreferenceSettings(user);

        return UserPreferenceSettingDto.builder()
                .isPreferenceSettingCreated(isPreferenceSettingCreated)
                .userTasteResponseDto(userTasteResponseDto)
                .build();
    }

    private boolean updatePreferenceSettings(User user) {
        boolean hasPreferences = true;

        PreferedPopup preferedPopup = user.getPreferedPopup();
        TastePopup tastePopup = user.getTastePopup();
        WhoWithPopup whoWithPopup = user.getWhoWithPopup();

        if (preferedPopup == null) {
            preferedPopup = createDefaultPreferedPopup();
            preferedPopupRepository.save(preferedPopup);
            user.updatePopupTaste(preferedPopup);
        }

        if (tastePopup == null) {
            tastePopup = createDefaultTastePopup();
            tastePopupRepository.save(tastePopup);
            user.updatePopupTaste(tastePopup);
        }

        if (whoWithPopup == null) {
            whoWithPopup = createDefaultWhoWithPopup();
            whoWithPopupRepository.save(whoWithPopup);
            user.updatePopupTaste(whoWithPopup);
        }

        userRepository.save(user);

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
