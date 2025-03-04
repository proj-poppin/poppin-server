package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.request.UpdateManagerInformDto;
import com.poppin.poppinserver.inform.dto.userInform.request.CreateUserInformDto;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.user.domain.User;

@UseCase
public interface PopupCommandUseCase {
    // 프록시 팝업 생성
    Popup createPopup(CreateManagerInformDto createManagerInformDto, String operationStatus, TastePopup tastePopup, PreferedPopup preferedPopup);
    Popup createPopup(CreateUserInformDto createUserInformDto, String operationStatus, TastePopup tastePopup, PreferedPopup preferedPopup);

    // 기존 팝업으로부터 프록시 팝업 생성
    Popup copyPopup(Popup popup, PreferedPopup proxyPrefered, TastePopup proxyTaste, String operationStatus);

    // 프록시 팝업 업데이트
    void updatePopup(Popup popup, UpdateManagerInformDto updateManagerInformDto, String operationStatus, User agent);

    // 팝업 대표사진 업데이트
    void updatePopupPosterUrl(Popup popup, PosterImage posterImage);

    // 팝업 삭제
    void deletePopup(Popup popup);
}
