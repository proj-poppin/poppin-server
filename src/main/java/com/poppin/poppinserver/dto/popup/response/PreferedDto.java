package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PreferedPopup;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
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
