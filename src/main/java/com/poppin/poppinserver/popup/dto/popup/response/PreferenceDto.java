package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.popup.domain.Popup;
import lombok.Builder;

@Builder
public record PreferenceDto(
        PreferredDto preferencePopupStore,
        TasteDto preferenceCategory
) {
    public static PreferenceDto fromPopup(Popup popup) {
        return PreferenceDto.builder()
                .preferencePopupStore(PreferredDto.fromEntity(popup.getPreferedPopup()))
                .preferenceCategory(TasteDto.fromEntity(popup.getTastePopup()))
                .build();
    }
}
