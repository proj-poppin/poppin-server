package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.interest.usercase.InterestCommandUseCase;
import com.poppin.poppinserver.popup.domain.BlockedPopup;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.blockedPopup.response.BlockedPopupDto;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.usecase.PopupQueryUseCase;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockedPopupService {
    private final BlockedPopupRepository blockedPopupRepository;

    private final UserQueryUseCase userQueryUseCase;
    private final PopupQueryUseCase popupQueryUseCase;
    private final InterestCommandUseCase interestCommandUseCase;

    public BlockedPopupDto createBlockedPopup(String strPopupId, Long userId) {
        Long popupId = Long.valueOf(strPopupId);

        User user = userQueryUseCase.findUserById(userId);
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

    // 차단한 팝업 ID 리스트 조회
    public List<String> findBlockedPopupList(User user) {
        return blockedPopupRepository.findAllByUserId(user)
                .stream()
                .map(blockedPopup -> blockedPopup.getId().toString())
                .toList();
    }
}
