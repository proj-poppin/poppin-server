package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.interest.usercase.InterestCommandUseCase;
import com.poppin.poppinserver.interest.usercase.InterestQueryUseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.blockedPopup.response.BlockedPopupDto;
import com.poppin.poppinserver.popup.domain.BlockedPopup;
import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.interest.repository.InterestRepository;
import com.poppin.poppinserver.user.repository.UserRepository;
import com.poppin.poppinserver.user.usecase.ReadUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockedPopupService {
    private final BlockedPopupRepository blockedPopupRepository;

    private final ReadUserUseCase readUserUseCase;
    private final PopupQueryUseCase popupQueryUseCase;
    private final InterestCommandUseCase interestCommandUseCase;

    public BlockedPopupDto createBlockedPopup(String strPopupId, Long userId) {
        Long popupId = Long.valueOf(strPopupId);

        User user = readUserUseCase.findUserById(userId);
        Popup popup = popupQueryUseCase.findPopupById(popupId);

        // 관심 저장 해제
        interestCommandUseCase.deleteExistByUserIdAndPopupId(userId, popupId);

        // 팝업 차단 생성 및 저장
        BlockedPopup blockedPopup = BlockedPopup.builder()
                .userId(user)
                .popupId(popup)
                .build();

        blockedPopup = blockedPopupRepository.save(blockedPopup);

        return BlockedPopupDto.fromEntity(blockedPopup);
    } // 부트스트랩
}
