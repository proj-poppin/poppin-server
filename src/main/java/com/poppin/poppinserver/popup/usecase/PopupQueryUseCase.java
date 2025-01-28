package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;

@UseCase
public interface PopupQueryUseCase {
    Popup findPopupById(Long popupId);

    Boolean existsPopupById(Long popupId);
}
