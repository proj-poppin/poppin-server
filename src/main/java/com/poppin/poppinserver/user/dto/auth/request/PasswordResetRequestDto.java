package com.poppin.poppinserver.user.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record PasswordResetRequestDto(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "올바른 이메일 형식이 아닙니다.")
        String email,
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z\\d])(?=.*[!@#$%^&*()]).{8,}$", message = "올바른 비밀번호 형식이 아닙니다.")
        String password,
        @NotBlank(message = "비밀번호 확인을 입력하세요.")
        String passwordConfirm
) {
}
