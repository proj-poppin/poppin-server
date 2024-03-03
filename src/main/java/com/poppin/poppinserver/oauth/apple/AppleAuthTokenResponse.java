package com.poppin.poppinserver.oauth.apple;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AppleAuthTokenResponse(
        String accessToken,
        String tokenType,
        String expiresIn,
        String refreshToken,
        String idToken
) {
}
