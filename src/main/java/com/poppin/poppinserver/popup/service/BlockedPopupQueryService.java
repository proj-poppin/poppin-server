package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.repository.BlockedPopupRepository;
import com.poppin.poppinserver.popup.usecase.BlockedPopupQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockedPopupQueryService implements BlockedPopupQueryUseCase {
    private final BlockedPopupRepository blockedPopupRepository;

    @Override
    public Boolean existBlockedPopupByUserIdAndPopupId(Long userId, Long popupId) {
        return blockedPopupRepository.existsByPopupIdAndUserId(userId, popupId);
    }
}
