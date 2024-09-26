package com.poppin.poppinserver.user.dto.auth.response;

import lombok.Builder;

@Builder
public record AuthCodeResponseDto(
        String authCode
) {
}
