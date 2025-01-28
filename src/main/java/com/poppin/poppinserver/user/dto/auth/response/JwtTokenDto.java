package com.poppin.poppinserver.user.dto.auth.response;

import jakarta.validation.constraints.NotBlank;

public record JwtTokenDto(
        @NotBlank String accessToken,
        @NotBlank String refreshToken
) {
    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
