package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

@Builder
public record UserAdministrationDto(
        Long id,
        String email,
        String nickname,
        boolean requiresSpecialCare
) {
}
