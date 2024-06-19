package com.poppin.poppinserver.dto.report.response;

import lombok.Builder;

@Builder
public record ReportContentDto(
        Long reportId,
        String reporter,
        String reportedAt,
        String content
) {
}
