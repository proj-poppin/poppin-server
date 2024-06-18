package com.poppin.poppinserver.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedPopupListResponseDto(
        Long reportedPopupId,
        Long popupId,
        String reporter,
        String reportedAt,
        String popupName,
        boolean executed
) {
}
