package com.poppin.poppinserver.inform.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;

@UseCase
public interface UserInformCommandUseCase {
    // 팝업과 관련된 모든 사용자 제보 삭제
    void deleteAllUserInformByPopup(Popup popup);
}
