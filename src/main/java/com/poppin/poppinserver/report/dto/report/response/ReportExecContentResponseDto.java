package com.poppin.poppinserver.report.dto.report.response;

import lombok.Builder;

@Builder
public record ReportExecContentResponseDto(
        Long reportId,
        String adminName,
        String executedAt,
        String content
) {
}
