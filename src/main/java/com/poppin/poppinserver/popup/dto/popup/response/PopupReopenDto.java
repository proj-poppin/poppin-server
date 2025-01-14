package com.poppin.poppinserver.popup.dto.popup.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PopupReopenDto(

        @NotNull
        PopupStoreDto updatedPopup,
        @NotNull
        PopupWaitingDto newPopupWaiting
) {
    public static PopupReopenDto fromEntity(PopupStoreDto popupStoreDto, PopupWaitingDto popupWaitingDto){
        return PopupReopenDto.builder()
                .updatedPopup(popupStoreDto)
                .newPopupWaiting(popupWaitingDto)
                .build();
    }
}
