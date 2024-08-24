package com.poppin.poppinserver.report.dto.report.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReportedReviewDetailDto(
        Long reviewId,
        String reviewWriter,
        Long reviewCnt,
        String reviewContent,
        String reviewCreatedAt,
        boolean isCertificated,
        List<String> imageUrl, /* 후기 사진 리스트 */
        String userProfileImageUrl /* 후기 작성자 프로필 이미지 */
) {
}
