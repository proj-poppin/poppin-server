package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.interest.domain.Interest;
import lombok.Builder;

@Builder
public record PopupScrapDto(
        Long popupId,
        Long userId,
        String createdAt
) {
    public static PopupScrapDto fromInterest(Interest interest) {
        return PopupScrapDto.builder()
                .popupId(interest.getId().getPopupId())
                .userId(interest.getId().getUserId())
                .createdAt(interest.getCreatedAt().toString())
                .build();

    }
}
