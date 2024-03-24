package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.PreferedPopup;
import com.poppin.poppinserver.domain.TastePopup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.domain.WhoWithPopup;
import com.poppin.poppinserver.dto.popup.request.CreateUserTasteDto;
import com.poppin.poppinserver.dto.popup.response.PreferedDto;
import com.poppin.poppinserver.dto.popup.response.TasteDto;
import com.poppin.poppinserver.dto.popup.response.UserTasteDto;
import com.poppin.poppinserver.dto.popup.response.WhoWithDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PreferedPopupRepository;
import com.poppin.poppinserver.repository.TastePopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.repository.WhoWithPopupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PreferedPopupRepository preferedPopupRepository;
    private final TastePopupRepository tastePopupRepository;
    private final WhoWithPopupRepository whoWithPopupRepository;

    @Transactional
    public UserTasteDto createUserTaste(
            Long userId,
            CreateUserTasteDto createUserTasteDto
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (user.getPreferedPopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        PreferedPopup preferedPopup = PreferedPopup.builder()
                .market(createUserTasteDto.preferedDto().market())
                .display(createUserTasteDto.preferedDto().display())
                .experience(createUserTasteDto.preferedDto().experience())
                .wantFree(createUserTasteDto.preferedDto().wantFree())
                .build();
        preferedPopupRepository.save(preferedPopup);

        if (user.getTastePopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        TastePopup tastePopup = TastePopup.builder()
                .fasionBeauty(createUserTasteDto.tasteDto().fasionBeauty())
                .characters(createUserTasteDto.tasteDto().characters())
                .foodBeverage(createUserTasteDto.tasteDto().foodBeverage())
                .webtoonAni(createUserTasteDto.tasteDto().webtoonAni())
                .interiorThings(createUserTasteDto.tasteDto().interiorThings())
                .movie(createUserTasteDto.tasteDto().movie())
                .musical(createUserTasteDto.tasteDto().musical())
                .sports(createUserTasteDto.tasteDto().sports())
                .game(createUserTasteDto.tasteDto().game())
                .itTech(createUserTasteDto.tasteDto().itTech())
                .kpop(createUserTasteDto.tasteDto().kpop())
                .alchol(createUserTasteDto.tasteDto().alchol())
                .animalPlant(createUserTasteDto.tasteDto().animalPlant())
                .build();
        tastePopupRepository.save(tastePopup);

        if (user.getWhoWithPopup() != null) {
            throw new CommonException(ErrorCode.ALREADY_EXISTS_PREFERENCE);
        }
        WhoWithPopup whoWithPopup = WhoWithPopup.builder()
                .solo(createUserTasteDto.whoWithDto().solo())
                .withFriend(createUserTasteDto.whoWithDto().withFriend())
                .withFamily(createUserTasteDto.whoWithDto().withFamily())
                .withLover(createUserTasteDto.whoWithDto().withLover())
                .build();
        whoWithPopupRepository.save(whoWithPopup);

        return UserTasteDto.builder()
                .preferedDto(PreferedDto.fromEntity(preferedPopup))
                .tasteDto(TasteDto.fromEntity(tastePopup))
                .whoWithDto(WhoWithDto.fromEntity(whoWithPopup))
                .build();
    }

    public UserTasteDto readUserTaste(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        return UserTasteDto.builder()
                .preferedDto(PreferedDto.fromEntity(user.getPreferedPopup()))
                .tasteDto(TasteDto.fromEntity(user.getTastePopup()))
                .whoWithDto(WhoWithDto.fromEntity(user.getWhoWithPopup()))
                .build();
    }
}
