package com.poppin.poppinserver.report.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedPopupInfoDto(
        ReportedPopupDetailDto reportedPopupDetailDto,
        ReportContentDto reportContentDto
) {
}
