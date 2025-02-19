package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;

import java.util.List;

@UseCase
public interface TastedPopupCommandUseCase {
    // 프록시 TastedPopup 생성
    TastePopup createProxyTastePopup(TastePopup tastePopup);

    // TastedPopup 생성
    TastePopup createTastePopup(TastePopup tastePopup);
    TastePopup createTastePopup(CreateTasteDto createTasteDto);
    TastePopup createTastePopup(List<String> taste);

    // TastedPopup 수정
    void updateTastePopup(TastePopup tastePopup, CreateTasteDto createTasteDto);
}
