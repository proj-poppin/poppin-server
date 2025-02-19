package com.poppin.poppinserver.inform.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;

@UseCase
public interface ManagerInformCommandUseCase {
    // 팝업과 관련된 모든 운영자 제보 삭제
    void deleteAllManagerInformByPopup(Popup popup);
}
