package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.PreferedPopup;
import com.poppin.poppinserver.domain.TastePopup;
import com.poppin.poppinserver.domain.WhoWithPopup;
import lombok.Builder;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserTasteDto(
        PreferedDto preferedDto,
        TasteDto tasteDto,
        WhoWithDto whoWithDto
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
                        .preferedDto(preferedDto)
                        .tasteDto(tasteDto)
                        .whoWithDto(whoWithDto)
                        .build();
        }
}
