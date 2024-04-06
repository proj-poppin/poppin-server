package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.PreferedPopup;
import com.poppin.poppinserver.domain.TastePopup;
import com.poppin.poppinserver.domain.WhoWithPopup;
import lombok.Builder;

@Builder
public record UserTasteDto(
        PreferedDto preference,
        TasteDto taste,
        WhoWithDto whoWith
) {
        public static UserTasteDto fromEntity(
                PreferedPopup preferedPopup,
                TastePopup tastePopup,
                WhoWithPopup whoWithPopup
        ){
                PreferedDto preferedDto = PreferedDto.fromEntity(preferedPopup);
                TasteDto tasteDto = TasteDto.fromEntity(tastePopup);
                WhoWithDto whoWithDto = WhoWithDto.fromEntity(whoWithPopup);

                return UserTasteDto.builder()
                        .preference(preferedDto)
                        .taste(tasteDto)
                        .whoWith(whoWithDto)
                        .build();
        }
}
