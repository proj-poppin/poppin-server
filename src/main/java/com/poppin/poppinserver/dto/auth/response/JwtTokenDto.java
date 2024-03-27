package com.poppin.poppinserver.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record JwtTokenDto(
        @NotBlank String accessToken,
        @NotBlank String refreshToken
)
{
    @Builder
    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
