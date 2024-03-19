package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record WhoWithDto(
        Long id,
        Boolean solo,
        Boolean withFriend,
        Boolean withFamily,
        Boolean withBool) {
        public static WhoWithDto fromEntity(WhoWithDto whoWithDto){
                return WhoWithDto.builder()
                        .id(whoWithDto.id)
                        .solo(whoWithDto.solo)
                        .withFriend(whoWithDto.withFriend)
                        .withFamily(whoWithDto.withFamily)
                        .withBool(whoWithDto.withBool)
                        .build();
        }
}
