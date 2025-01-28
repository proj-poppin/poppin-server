package com.poppin.poppinserver.report.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.report.dto.report.request.CreatePopupReportDto;
import com.poppin.poppinserver.report.dto.report.request.CreateReviewReportDto;
import com.poppin.poppinserver.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 신고")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    /* 유저 후기 신고 */
    @Operation(summary = "유저 후기 신고")
    @PostMapping("/reviews")
    public ResponseDto<?> createReviewReport(@Parameter(hidden = true) @UserId Long userId,
                                             @RequestBody CreateReviewReportDto createReviewReportDto) {
        reportService.createReviewReport(userId, createReviewReportDto);
        return ResponseDto.created("후기 신고가 접수되었습니다.");
    }

    /* 유저 팝업 신고 */
    @Operation(summary = "유저 팝업 신고")
    @PostMapping("/popups")
    public ResponseDto<?> createPopupReport(@Parameter(hidden = true) @UserId Long userId,
                                            @RequestBody CreatePopupReportDto createPopupReportDto) {
        reportService.createPopupReport(userId, createPopupReportDto);
        return ResponseDto.created("팝업 신고가 접수되었습니다.");
    }

}
