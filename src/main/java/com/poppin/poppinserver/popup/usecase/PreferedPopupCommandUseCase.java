package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;

import java.util.List;

@UseCase
public interface PreferedPopupCommandUseCase {
    // 프록시 PreferedPopup 생성
    PreferedPopup createProxyPreferedPopup(PreferedPopup preferedPopup);

    // PreferedPopup 생성
    PreferedPopup createPreferedPopup(PreferedPopup preferedPopup);
    PreferedPopup createPreferedPopup(CreatePreferedDto createPreferedDto);
    PreferedPopup createPreferedPopup(List<String> prepered);
    PreferedPopup createEmptyPreferedPopup();

    // PreferedPopup 수정
    void updatePreferedPopup(PreferedPopup preferedPopup, CreatePreferedDto createPreferedDto);
}
