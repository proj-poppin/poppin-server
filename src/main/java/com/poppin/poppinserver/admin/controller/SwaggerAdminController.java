package com.poppin.poppinserver.admin.controller;

import com.poppin.poppinserver.admin.dto.request.AdminFaqRequestDto;
import com.poppin.poppinserver.admin.dto.response.AdminFaqResponseDto;
import com.poppin.poppinserver.admin.dto.response.AdminInfoResponseDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.InformApplyResponseDto;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.report.dto.report.request.CreateReportExecContentDto;
import com.poppin.poppinserver.report.dto.report.response.ReportExecContentResponseDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedPopupInfoDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedPopupListResponseDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedReviewInfoDto;
import com.poppin.poppinserver.report.dto.report.response.ReportedReviewListResponseDto;
import com.poppin.poppinserver.user.dto.auth.response.JwtTokenDto;
import com.poppin.poppinserver.user.dto.user.response.UserAdministrationDetailDto;
import com.poppin.poppinserver.user.dto.user.response.UserListDto;
import com.poppin.poppinserver.user.dto.user.response.UserReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자(관리자)", description = "관리자 전용 유저 관련 기능을 제공합니다.")
public interface SwaggerAdminController {

    @Operation(summary = "관리자 로그인", description = "관리자 계정을 사용하여 로그인을 수행합니다.")
    @PostMapping("/sign-in")
    ResponseDto<JwtTokenDto> authSignIn(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String authorizationHeader);

    @Operation(summary = "관리자 토큰 재발급", description = "관리자의 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    @PostMapping("/refresh")
    ResponseDto<JwtTokenDto> refresh(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String refreshToken);

    @Operation(summary = "FAQ 조회", description = "등록된 FAQ 목록을 조회합니다.")
    @GetMapping("/support/faqs")
    ResponseDto<List<AdminFaqResponseDto>> readFaqs();

    @Operation(summary = "FAQ 생성", description = "새로운 FAQ를 등록합니다.")
    @PostMapping("/support/faqs")
    ResponseDto<AdminFaqResponseDto> createFaq(
            @Parameter(hidden = true) Long adminId,
            @RequestBody AdminFaqRequestDto adminFaqRequestDto
    );

    @Operation(summary = "FAQ 삭제", description = "특정 FAQ를 삭제합니다.")
    @DeleteMapping("/support/faqs/{faqId}")
    ResponseDto<String> deleteFaq(@PathVariable Long faqId);

    @Operation(summary = "회원 목록 조회", description = "관리자가 회원 목록을 조회합니다.")
    @GetMapping("/users")
    ResponseDto<UserListDto> readUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "44") int size,
            @RequestParam(value = "care") Boolean care
    );

    @Operation(summary = "회원 상세 조회", description = "특정 회원의 상세 정보를 조회합니다.")
    @GetMapping("/users/{userId}")
    ResponseDto<UserAdministrationDetailDto> readUserDetail(@PathVariable Long userId);

    @Operation(summary = "회원 검색", description = "특정 조건을 사용하여 회원을 검색합니다.")
    @GetMapping("/users/search")
    ResponseDto<UserListDto> searchUsers(@RequestParam("text") String text);

    @Operation(summary = "회원 후기 조회", description = "특정 회원이 작성한 후기 목록을 조회합니다.")
    @GetMapping("/users/{userId}/reviews")
    ResponseDto<PagingResponseDto<List<UserReviewDto>>> readUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(value = "hidden") Boolean hidden
    );

    @Operation(summary = "후기 신고 목록 조회", description = "후기 신고 목록을 조회합니다.")
    @GetMapping("/reports/reviews")
    ResponseDto<PagingResponseDto<List<ReportedReviewListResponseDto>>> readReviewReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "19") int size,
            @RequestParam("isExec") Boolean isExec
    );

    @Operation(summary = "후기 신고 상세 조회", description = "특정 후기 신고의 상세 정보를 조회합니다.")
    @GetMapping("/reports/reviews/{reportId}")
    ResponseDto<ReportedReviewInfoDto> readReviewReportDetail(@PathVariable Long reportId);

    @Operation(summary = "후기 신고 처리 생성", description = "후기 신고 처리를 생성합니다.")
    @PostMapping("/reports/reviews/{reportId}")
    ResponseDto<String> processReviewReport(
            @Parameter(hidden = true) Long adminId,
            @PathVariable Long reportId,
            @RequestBody CreateReportExecContentDto createReportExecContentDto
    );

    @Operation(summary = "후기 신고 처리 - 변경 사항 없음", description = "변경 사항 없이 후기 신고를 처리합니다.")
    @PostMapping("/reports/reviews/{reportId}/exec")
    ResponseDto<String> processReviewReportExec(
            @Parameter(hidden = true) Long adminId,
            @PathVariable Long reportId
    );

    @Operation(summary = "후기 신고 처리 내용 조회", description = "후기 신고 처리 내용을 조회합니다.")
    @GetMapping("/reports/reviews/{reportId}/exec")
    ResponseDto<ReportExecContentResponseDto> readReviewReportExecContent(@PathVariable Long reportId);

    @Operation(summary = "팝업 신고 목록 조회", description = "팝업 신고 목록을 조회합니다.")
    @GetMapping("/reports/popups")
    ResponseDto<PagingResponseDto<List<ReportedPopupListResponseDto>>> readPopupReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "19") int size,
            @RequestParam("isExec") Boolean isExec
    );

    @Operation(summary = "팝업 신고 상세 조회", description = "특정 팝업 신고의 상세 정보를 조회합니다.")
    @GetMapping("/reports/popups/{reportId}")
    ResponseDto<ReportedPopupInfoDto> readPopupReportDetail(@PathVariable Long reportId);

    @Operation(summary = "팝업 신고 처리 내용 조회", description = "팝업 신고 처리 내용을 조회합니다.")
    @GetMapping("/reports/popups/{reportId}/exec")
    ResponseDto<ReportExecContentResponseDto> readPopupReportExecContent(@PathVariable Long reportId);

    @Operation(summary = "팝업 신고 처리 생성", description = "팝업 신고 처리를 생성합니다.")
    @PostMapping("/reports/popups/{reportId}")
    ResponseDto<String> processPopupReport(
            @Parameter(hidden = true) Long adminId,
            @PathVariable Long reportId,
            @RequestBody CreateReportExecContentDto createReportExecContentDto
    );

    @Operation(summary = "정보 생성", description = "관리자가 새로운 정보를 생성합니다.")
    @PostMapping(value = "/info/create", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseDto<InformApplyResponseDto> createInformation(
            @Parameter(hidden = true) Long adminId,
            @RequestPart("contents") InformAlarmCreateRequestDto requestDto,
            @RequestPart("images") MultipartFile images
    );

    @Operation(summary = "관리자 계정 정보", description = "관리자의 계정 정보를 가져옵니다.")
    @GetMapping(value = "/info")
    ResponseDto<AdminInfoResponseDto> readAdminInfo(
            @Parameter(hidden = true) Long adminId
    );
}
