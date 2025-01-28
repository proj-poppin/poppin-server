//package com.poppin.poppinserver.report.controller.swagger;
//
//import com.poppin.poppinserver.core.dto.ResponseDto;
//import com.poppin.poppinserver.report.dto.report.request.CreatePopupReportDto;
//import com.poppin.poppinserver.report.dto.report.request.CreateReviewReportDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@Tag(name = "유저 신고")
//public interface SwaggerReportController {
//
//    @Operation(summary = "유저 후기 신고")
//    ResponseDto<?> createReviewReport(@Parameter(hidden = true) Long userId, @RequestBody CreateReviewReportDto createReviewReportDto);
//
//    @Operation(summary = "유저 팝업 신고")
//    ResponseDto<?> createPopupReport(@Parameter(hidden = true) Long userId, @RequestBody CreatePopupReportDto createPopupReportDto);
//}
