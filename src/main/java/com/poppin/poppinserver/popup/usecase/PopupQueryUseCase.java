package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupSummaryDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupTasteDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@UseCase
public interface PopupQueryUseCase {
    Popup findPopupById(Long popupId);

    Popup findPopupByIdElseNull(Long popupId);

    List<Popup> findHotPopupList();

    List<Popup> findHotPopupList(Long userId);

    List<Popup> findNewPopupList();

    List<Popup> findNewPopupList(Long userId);

    List<Popup> findClosingPopupList();

    List<Popup> findClosingPopupList(Long userId);
}
