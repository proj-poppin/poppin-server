package com.poppin.poppinserver.dto.user.response;

import com.poppin.poppinserver.type.ELoginProvider;
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
