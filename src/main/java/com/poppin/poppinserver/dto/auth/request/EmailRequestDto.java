package com.poppin.poppinserver.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailRequestDto(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식이 아닙니다.")
        String email
) {
}
