package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;

@UseCase
public interface BlockedPopupQueryUseCase {
    // 차단 내역이 존재하는지 검색
    Boolean existBlockedPopupByUserIdAndPopupId(Long userId, Long popupId);
}
