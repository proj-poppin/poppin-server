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
    // 팝업 조회
    Popup findPopupById(Long popupId);

    // 팝업 조회 없으면 null
    Popup findPopupByIdElseNull(Long popupId);

    // 인기 top5 팝업
    List<Popup> findHotPopupList();
    List<Popup> findHotPopupList(Long userId);

    // 새로 오픈 팝업 5개
    List<Popup> findNewPopupList();
    List<Popup> findNewPopupList(Long userId);

    // 종료 임박 팝업 5개
    List<Popup> findClosingPopupList();
    List<Popup> findClosingPopupList(Long userId);

    // 취향저격 팝업
    List<Popup> findRecommandPopupList(Long userId);
}
