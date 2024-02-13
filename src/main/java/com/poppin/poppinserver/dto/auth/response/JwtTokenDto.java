package com.poppin.poppinserver.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record JwtTokenDto(
        @JsonProperty("access_token")
        @NotBlank String accessToken,
        @JsonProperty("refresh_token")
        @NotBlank String refreshToken
)
{
    @Builder
    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
