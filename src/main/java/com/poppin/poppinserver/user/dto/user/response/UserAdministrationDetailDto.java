package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import lombok.Builder;

@Builder
public record UserAdministrationDetailDto(
        Long id,
        String userImageUrl,
        String email,
        String nickname,
        ELoginProvider provider,
        boolean requiresSpecialCare,
        Long hiddenReviewCount
) {
}
