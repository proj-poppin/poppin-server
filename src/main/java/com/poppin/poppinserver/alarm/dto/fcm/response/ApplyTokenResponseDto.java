package com.poppin.poppinserver.alarm.dto.fcm.response;

import com.poppin.poppinserver.alarm.dto.fcm.request.ApplyTokenRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ApplyTokenResponseDto(

        @NotNull
        String token,

        @NotNull
        String device ,// android or ios

        @NotNull
        String response,

        @NotNull
        String description

) {
    public static ApplyTokenResponseDto fromEntity(ApplyTokenRequestDto applyTokenRequestDto, String response, String description){
        return ApplyTokenResponseDto.builder()
                .token(applyTokenRequestDto.fcmToken())
                .device(applyTokenRequestDto.device())
                .response(response)
                .description(description)
                .build();
    }
}
