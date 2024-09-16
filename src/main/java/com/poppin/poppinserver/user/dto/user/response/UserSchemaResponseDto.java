package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.user.domain.User;
import lombok.Builder;

@Builder
public record UserSchemaResponseDto(
        String userImageUrl,
        String email,
        String nickname,
        String accountType,
        Integer writtenReview,
        Integer visitedPopupCnt
) {
    public static UserSchemaResponseDto fromUserEntity(User user) {
        return UserSchemaResponseDto.builder()
                .userImageUrl(user.getProfileImageUrl())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .accountType(user.getProvider().toString())
                .writtenReview(user.getReviewCnt())
                .visitedPopupCnt(user.getVisitedPopupCnt())
                .build();
    }
}
