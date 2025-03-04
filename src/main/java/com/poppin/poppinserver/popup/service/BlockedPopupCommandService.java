package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.BlockedPopupCommandRepository;
import com.poppin.poppinserver.popup.repository.BlockedPopupQueryRepository;
import com.poppin.poppinserver.popup.usecase.BlockedPopupCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockedPopupCommandService implements BlockedPopupCommandUseCase {
    private final BlockedPopupCommandRepository blockedPopupCommandRepository;

    @Override
    public void deleteAllBlockedPopup(Popup popup) {
        blockedPopupCommandRepository.deleteAllByPopupId(popup);
    }

    @Override
    public void deleteAllBlockedPopup(Long userId) {
        blockedPopupCommandRepository.deleteAllByUserId(userId);
    }
}
