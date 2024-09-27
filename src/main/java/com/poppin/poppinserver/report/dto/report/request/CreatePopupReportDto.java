package com.poppin.poppinserver.report.dto.report.request;

import jakarta.validation.constraints.NotNull;

public record CreatePopupReportDto(
        @NotNull(message = "신고 내용을 입력하세요.") String content,
        @NotNull(message = "popupId 누락") Long popupId
) {
}
