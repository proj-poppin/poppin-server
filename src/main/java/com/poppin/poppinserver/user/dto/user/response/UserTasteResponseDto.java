package com.poppin.poppinserver.user.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.popup.dto.popup.response.PreferedDto;
import com.poppin.poppinserver.popup.dto.popup.response.TasteDto;
import com.poppin.poppinserver.popup.dto.popup.response.WhoWithDto;
import lombok.Builder;

@Builder
public record UserTasteResponseDto(
        @JsonProperty("preferencePopupStore") PreferedDto preference,
        @JsonProperty("preferenceCategory") TasteDto taste,
        @JsonProperty("preferenceCompanion") WhoWithDto whoWith
) {
    public static UserTasteResponseDto fromEntity(
            PreferedPopup preferedPopup,
            TastePopup tastePopup,
            WhoWithPopup whoWithPopup
    ) {
        PreferedDto preferedDto = PreferedDto.fromEntity(preferedPopup);
        TasteDto tasteDto = TasteDto.fromEntity(tastePopup);
        WhoWithDto whoWithDto = WhoWithDto.fromEntity(whoWithPopup);

        return UserTasteResponseDto.builder()
                .preference(preferedDto)
                .taste(tasteDto)
                .whoWith(whoWithDto)
                .build();
    }
}
