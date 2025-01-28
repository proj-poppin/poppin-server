package com.poppin.poppinserver.admin.dto.response;

import lombok.Builder;

@Builder
public record UserAdministrationResponseDto(
        Long id,
        String email,
        String nickname,
        boolean requiresSpecialCare
) {
}
