package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.user.domain.User;
import lombok.Builder;

/**
 * 사용자 정보를 반환하기 위한 응답 DTO입니다.
 *
 * <p><b>사용 필드 설명:</b></p>
 * <ul>
 *   <li><b>userImageUrl</b>: 사용자의 프로필 이미지 URL</li>
 *   <li><b>email</b>: 사용자의 이메일 주소</li>
 *   <li><b>nickname</b>: 사용자의 닉네임</li>
 *   <li><b>accountType</b>: 사용자의 계정 유형 (소셜 로그인 제공자)</li>
 *   <li><b>writtenReview</b>: 사용자가 작성한 리뷰 개수</li>
 *   <li><b>visitedPopupCnt</b>: 사용자가 방문한 팝업 스토어 개수</li>
 * </ul>
 *
 * @author wonjun
 */
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
