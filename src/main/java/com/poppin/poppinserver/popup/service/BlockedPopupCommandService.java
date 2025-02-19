package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.usecase.BlockedPopupCommandUseCase;
import com.poppin.poppinserver.popup.usecase.BlockedPopupQueryUseCase;
import com.poppin.poppinserver.user.repository.BlockedUserCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockedPopupCommandService implements BlockedPopupCommandUseCase {
    private final BlockedPopupRepository blockedPopupRepository;

    @Override
    public void deleteAllBlockedPopupByPopup(Popup popup) {
        blockedPopupRepository.deleteAllByPopupId(popup);
    }
}
