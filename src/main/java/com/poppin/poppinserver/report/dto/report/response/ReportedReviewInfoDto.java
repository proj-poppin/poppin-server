package com.poppin.poppinserver.report.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedReviewInfoDto(
        ReportedPopupDetailDto reportedPopupDetailDto,
        ReportedReviewDetailDto reportedReviewDetailDto,
        ReportContentDto reportContentDto
) {
}
