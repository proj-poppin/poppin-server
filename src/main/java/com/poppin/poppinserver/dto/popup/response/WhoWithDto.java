package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.WhoWithPopup;
import lombok.Builder;

@Builder
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
