package com.poppin.poppinserver.dto.report.request;

import lombok.Builder;

@Builder
public record CreateReportExecContentDto(
        String content
) {
}
