package com.poppin.poppinserver.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedReviewListResponseDto(
        Long reportId,
        Long reviewId,
        String reporter,
        String reportedAt,
        String popupName,
        boolean executed
) {
}
