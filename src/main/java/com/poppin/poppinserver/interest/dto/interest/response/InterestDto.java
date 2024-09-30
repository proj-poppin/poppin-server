package com.poppin.poppinserver.interest.dto.interest.response;

import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.response.PopupScrapDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.visit.dto.visitorData.response.VisitorDataInfoDto;
import lombok.Builder;

import java.util.Optional;

@Builder
public record InterestDto(
        PopupStoreDto updatedPopup,
        PopupScrapDto newPopupScrap
) {
    public static InterestDto fromEntity(Interest intereste, Popup popup, VisitorDataInfoDto visitorDataDto, Optional<Integer> visitorCnt) {
        return InterestDto.builder()
                .updatedPopup(PopupStoreDto.fromEntity(popup, visitorDataDto, visitorCnt))
                .newPopupScrap(PopupScrapDto.fromInterest(intereste))
                .build();
    }
}
