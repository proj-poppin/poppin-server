package com.poppin.poppinserver.dto.blockedPopup.response;

import com.poppin.poppinserver.domain.BlockedPopup;
import lombok.Builder;

@Builder
public record BlockedPopupDto(
        Long blockedPopupId,
        Long popupId,
        Long userId
) {
    static public BlockedPopupDto fromEntity(BlockedPopup blockedPopup) {
        return BlockedPopupDto.builder()
                .popupId(blockedPopup.getPopupId().getId())
                .userId(blockedPopup.getUserId().getId())
                .blockedPopupId(blockedPopup.getId())
                .build();
    }
}
