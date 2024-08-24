package com.poppin.poppinserver.report.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedPopupListResponseDto(
        Long reportId,
        Long popupId,
        String reporter,
        String reportedAt,
        String popupName,
        boolean executed
) {
}
