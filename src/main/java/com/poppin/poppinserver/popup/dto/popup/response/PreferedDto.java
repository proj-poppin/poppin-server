package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.popup.domain.PreferedPopup;
import lombok.Builder;

@Builder
public record PreferedDto(
        Long id,
        Boolean market,
        Boolean display,
        Boolean experience,
        Boolean wantFree
) {
        public static PreferedDto fromEntity(PreferedPopup preferedPopup){
                return PreferedDto.builder()
                        .id(preferedPopup.getId())
                        .market(preferedPopup.getMarket())
                        .display(preferedPopup.getDisplay())
                        .experience(preferedPopup.getExperience())
                        .wantFree(preferedPopup.getWantFree())
                        .build();
        }
}
