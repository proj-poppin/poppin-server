package com.poppin.poppinserver.user.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SocialRegisterRequestDto(
        @NotNull @JsonProperty("accountType") String provider,
        @NotNull String fcmToken,
        @Size(min = 1, max = 10)
        @Pattern(regexp = "^(?=.*[가-힣A-Za-z])[가-힣A-Za-z\\s]*$", message = "올바른 닉네임 형식이 아닙니다.")
        @NotBlank String nickname
) {
}
