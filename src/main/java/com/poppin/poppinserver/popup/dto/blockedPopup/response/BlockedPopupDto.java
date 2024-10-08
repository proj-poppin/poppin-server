package com.poppin.poppinserver.popup.dto.blockedPopup.response;

import com.poppin.poppinserver.popup.domain.BlockedPopup;
import lombok.Builder;

@Builder
public record BlockedPopupDto(
        String blockedPopupId,
        String popupId,
        String userId
) {
    static public BlockedPopupDto fromEntity(BlockedPopup blockedPopup) {
        return BlockedPopupDto.builder()
                .popupId(String.valueOf(blockedPopup.getPopupId().getId()))
                .userId(String.valueOf(blockedPopup.getUserId().getId()))
                .blockedPopupId(String.valueOf(blockedPopup.getId()))
                .build();
    }
}
