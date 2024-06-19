package com.poppin.poppinserver.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedPopupInfoDto(
        ReportedPopupDetailDto reportedPopupDetailDto,
        ReportContentDto reportContentDto
) {
}
