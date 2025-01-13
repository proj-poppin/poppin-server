package com.poppin.poppinserver.popup.dto.popup.response;

import lombok.Builder;

@Builder
public record PopupWaitingDto(

        String id,      // 재오픈 신청 함 으로써 생기는 unique id
        String popupId  // 재오픈 신청한 팝업 id
) {
    public static PopupWaitingDto fromEntity(Long waitingId, Long popupId){
        return PopupWaitingDto.builder()
                .id(waitingId.toString())
                .popupId(popupId.toString())
                .build();
    }
}
