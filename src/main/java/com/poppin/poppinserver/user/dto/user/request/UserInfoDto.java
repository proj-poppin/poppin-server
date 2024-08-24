package com.poppin.poppinserver.user.dto.user.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserInfoDto(
        @NotEmpty
        @Size(min = 1, max = 10)
        @Pattern(regexp = "^(?=.*[가-힣A-Za-z])[가-힣A-Za-z\\s]*$", message = "올바른 닉네임 형식이 아닙니다.")
        String nickname
) {

}
