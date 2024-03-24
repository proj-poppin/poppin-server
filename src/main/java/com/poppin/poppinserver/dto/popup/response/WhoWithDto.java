package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.WhoWithPopup;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WhoWithDto(
        Long id,
        Boolean solo,
        Boolean withFriend,
        Boolean withFamily,
        Boolean withLover) {
        public static WhoWithDto fromEntity(WhoWithPopup whoWithPopup){
                return WhoWithDto.builder()
                        .id(whoWithPopup.getId())
                        .solo(whoWithPopup.getSolo())
                        .withFriend(whoWithPopup.getWithFriend())
                        .withFamily(whoWithPopup.getWithFamily())
                        .withLover(whoWithPopup.getWithLover())
                        .build();
        }
}
