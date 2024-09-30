package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import lombok.Builder;

// TODO: 리팩 후 삭제예정
@Builder
public record UserProfileDto(
        String userImageUrl,
        String email,
        String nickname,
        ELoginProvider provider
) {
}
