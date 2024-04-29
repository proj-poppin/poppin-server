package com.poppin.poppinserver.dto.notification.response;

import com.poppin.poppinserver.dto.notification.request.TokenRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TokenResponseDto(

        @NotNull
        String token,

        @NotNull
        String device // android or ios

) {
    public static TokenResponseDto fromEntity(TokenRequestDto tokenRequestDto){
        return TokenResponseDto.builder()
                .token(tokenRequestDto.token())
                .device(tokenRequestDto.device())
                .build();
    }
}
