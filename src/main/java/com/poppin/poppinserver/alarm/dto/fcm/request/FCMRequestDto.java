package com.poppin.poppinserver.alarm.dto.fcm.request;

import com.poppin.poppinserver.core.type.EPopupTopic;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FCMRequestDto(

        @NotNull
        String popupId,

        @NotNull
        String token,

        @NotNull
        String title,

        @NotNull
        String body,

        @NotNull
        EPopupTopic topic

) {
    public static FCMRequestDto fromEntity(Long popupId, String token, String title, String body, EPopupTopic topic) {
        return FCMRequestDto.builder()
                .popupId(String.valueOf(popupId))
                .token(token)
                .title(title)
                .body(body)
                .topic(topic)
                .build();
    }
}
