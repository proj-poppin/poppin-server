package com.poppin.poppinserver.report.dto.report.request;

import jakarta.validation.constraints.NotNull;

public record CreateReviewReportDto(
        @NotNull(message = "신고 내용을 입력하세요.") String content,
        @NotNull(message = "reviewId 누락") String reviewId
) {
}
