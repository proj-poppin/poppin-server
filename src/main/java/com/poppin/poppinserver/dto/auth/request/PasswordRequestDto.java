package com.poppin.poppinserver.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordRequestDto(
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z\\d])(?=.*[!@#$%^&*()]).{8,}$", message = "올바른 비밀번호 형식이 아닙니다.")
        String password,
        @NotBlank(message = "비밀번호 확인을 입력하세요.")
        String passwordConfirm
) {
}
