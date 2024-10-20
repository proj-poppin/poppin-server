package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;

@UseCase
public interface ReadPopupUseCase {
    Popup findPopupById(Long popupId);
}
