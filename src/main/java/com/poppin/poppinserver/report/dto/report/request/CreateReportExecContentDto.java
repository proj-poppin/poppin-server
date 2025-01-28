package com.poppin.poppinserver.report.dto.report.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateReportExecContentDto(
        @NotNull(message = "신고 처리 내용을 입력하세요.")
        String content
) {
}
