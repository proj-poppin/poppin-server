package com.poppin.poppinserver.interest.dto.interest.response;

import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.dto.popup.response.PopupScrapDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.popup.dto.popup.response.PopupDto;
import com.poppin.poppinserver.user.dto.user.response.UserDto;
import lombok.Builder;

@Builder
public record InterestDto(
        PopupStoreDto updatedPopup,
        PopupScrapDto newPopupScrap
) {
    public static InterestDto fromEntity(Interest intereste, Popup popup) {
        return InterestDto.builder()
                .updatedPopup(PopupStoreDto.fromEntity(popup))
                .newPopupScrap(PopupScrapDto.fromInterest(intereste))
                .build();
    }
}
