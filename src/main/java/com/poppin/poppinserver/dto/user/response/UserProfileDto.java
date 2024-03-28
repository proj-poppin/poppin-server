package com.poppin.poppinserver.dto.user.response;

import com.poppin.poppinserver.type.ELoginProvider;
import lombok.Builder;

@Builder
public record UserProfileDto(
        String userImageUrl,
        String email,
        String nickname,
        String birthDate,
        ELoginProvider provider
) {
}
