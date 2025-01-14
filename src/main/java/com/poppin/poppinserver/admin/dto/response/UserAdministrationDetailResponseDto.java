package com.poppin.poppinserver.admin.dto.response;

import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import lombok.Builder;

@Builder
public record UserAdministrationDetailResponseDto(
        Long id,
        String userImageUrl,
        String email,
        String nickname,
        ELoginProvider provider,
        boolean requiresSpecialCare,
        Long hiddenReviewCount
) {
}
