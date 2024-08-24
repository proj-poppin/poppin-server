package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.core.type.ELoginProvider;
import lombok.Builder;

@Builder
public record UserProfileDto(
        String userImageUrl,
        String email,
        String nickname,
        ELoginProvider provider
) {
}
