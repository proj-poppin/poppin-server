package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateWhoWithDto;

import java.util.List;

@UseCase
public interface WhoWithPopupCommandUseCase {
    // WhoWithPopup 생성
    WhoWithPopup createWhoWithPopup(WhoWithPopup whoWithPopup);

    // WhoWithPopup 수정
    WhoWithPopup updateWhoWithPopup(WhoWithPopup whoWithPopup, CreateWhoWithDto createWhoWithDto);
}
