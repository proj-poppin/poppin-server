package com.poppin.poppinserver.user.dto.user.response;

import lombok.Builder;

// TODO: 리팩 후 삭제예정
@Builder
public record UserMypageDto(
        String nickname,
        String userImageUrl,
        Integer writtenReview,
        Integer visitedPopupCnt
) {
}
