package com.poppin.poppinserver.dto.fcm.request;

import com.poppin.poppinserver.type.EPopupTopic;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record FCMRequestDto(


        @NotNull
        Long popupId,

        @NotNull
        String token,

        @NotNull
        String title,

        @NotNull
        String body,

        @NotNull
        EPopupTopic topic

) {
        public static FCMRequestDto fromEntity(Long popupId, String token, String title, String body, EPopupTopic topic){
                return FCMRequestDto.builder()
                        .popupId(popupId)
                        .token(token)
                        .title(title)
                        .body(body)
                        .topic(topic)
                        .build();
        }
}
