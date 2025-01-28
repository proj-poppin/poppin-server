package com.poppin.poppinserver.popup.dto.popup.response;

import lombok.Builder;

@Builder
public record PopupReopenDto(

        PopupStoreDto updatedPopup,
        PopupWaitingDto newPopupWaiting
) {
    public static PopupReopenDto fromEntity(PopupStoreDto popupStoreDto, PopupWaitingDto popupWaitingDto){
        return PopupReopenDto.builder()
                .updatedPopup(popupStoreDto)
                .newPopupWaiting(popupWaitingDto)
                .build();
    }
}
