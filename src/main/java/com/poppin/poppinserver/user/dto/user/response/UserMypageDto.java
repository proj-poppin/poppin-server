package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

@Builder
public record UserMypageDto(
        String nickname,
        String userImageUrl,
        Long writtenReview,
        Long visitedPopupCnt
) {
}
