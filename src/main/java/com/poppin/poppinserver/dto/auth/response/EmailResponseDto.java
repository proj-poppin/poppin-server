package com.poppin.poppinserver.dto.auth.response;

import lombok.Builder;

@Builder
public record EmailResponseDto(
        String authCode
) {
}
