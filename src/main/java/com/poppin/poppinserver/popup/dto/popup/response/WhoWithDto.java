package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import lombok.Builder;

@Builder
public record WhoWithDto(
        Boolean solo,   // 혼자
        Boolean withFriend, // 친구와 함께
        Boolean withFamily, // 가족과 함께
        Boolean withLover   // 연인과 함께
) {
    public static WhoWithDto fromEntity(WhoWithPopup whoWithPopup) {
        return WhoWithDto.builder()
                .solo(whoWithPopup.getSolo())
                .withFriend(whoWithPopup.getWithFriend())
                .withFamily(whoWithPopup.getWithFamily())
                .withLover(whoWithPopup.getWithLover())
                .build();
    }
}
