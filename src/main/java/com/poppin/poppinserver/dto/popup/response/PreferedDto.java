package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Popup;
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
        public static PreferedDto fromEntity(PreferedDto preferedDto){
                return PreferedDto.builder()
                        .id(preferedDto.id)
                        .market(preferedDto.market)
                        .display(preferedDto.display)
                        .experience(preferedDto.experience)
                        .wantFree(preferedDto.wantFree)
                        .build();
        }
}
