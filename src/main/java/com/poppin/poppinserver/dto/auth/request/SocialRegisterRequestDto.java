package com.poppin.poppinserver.dto.auth.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.type.ELoginProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SocialRegisterRequestDto(
        @NotNull ELoginProvider provider,
        @Size(min = 1, max = 10)
        @Pattern(regexp = "^(?=.*[가-힣A-Za-z])[가-힣A-Za-z\\s]*$", message = "올바른 닉네임 형식이 아닙니다.")
        @NotBlank String nickname,
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "올바른 생년월일 형식이 아닙니다.")
        @NotBlank String birthDate
) {
}
