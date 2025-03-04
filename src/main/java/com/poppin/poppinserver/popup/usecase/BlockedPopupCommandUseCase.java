package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;

@UseCase
public interface BlockedPopupCommandUseCase {
    // 관련된 모든 차단내역 삭제
    void deleteAllBlockedPopup(Popup popup);

    // 관련된 모든 차단내역 삭제
    void deleteAllBlockedPopup(Long userId);
}
