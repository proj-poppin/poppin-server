package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;

@UseCase
public interface BlockedPopupQueryUseCase {
    Boolean existBlockedPopupByUserIdAndPopupId(Long userId, Long popupId);
}
