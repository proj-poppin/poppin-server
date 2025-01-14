package com.poppin.poppinserver.user.dto.auth.response;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record AuthCodeResponseDto(
        String authCode
) {
    public static AuthCodeResponseDto fromAuthCode(String authCode) {
        return AuthCodeResponseDto.builder()
                .authCode(authCode)
                .build();
    }
}
