package com.poppin.poppinserver.user.controller;

import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.constant.Constant;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.report.dto.report.request.CreateReportExecContentDto;
import com.poppin.poppinserver.user.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.user.service.AdminService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    /* 관리자용 로그인 */
    @PostMapping("/sign-in")
    public ResponseDto<?> authSignIn(
            @NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String authorizationHeader
    ) {
        return ResponseDto.ok(adminService.authSignIn(authorizationHeader));
    }

    /* FAQ 조회 */
    @GetMapping("/support/faqs")
    public ResponseDto<?> readFaqs() {
        return ResponseDto.ok(adminService.readFAQs());
    }

    /* FAQ 생성 */
    @PostMapping("/support/faqs")
    public ResponseDto<?> createFaq(@UserId Long adminId, @RequestBody FaqRequestDto faqRequestDto) {
        return ResponseDto.created(adminService.createFAQ(adminId, faqRequestDto));
    }

    /* FAQ 삭제 */
    @DeleteMapping("/support/faqs/{faqId}")
    public ResponseDto<?> deleteFaq(@PathVariable Long faqId) {
        adminService.deleteFAQ(faqId);
        return ResponseDto.ok("FAQ가 삭제되었습니다.");
    }

    /* 회원 관리 목록 조회 */
    @GetMapping("/users")
    public ResponseDto<?> readUsers(@RequestParam(required = false, defaultValue = "0") int page,
                                    @RequestParam(required = false, defaultValue = "44") int size,
                                    @RequestParam(value = "care") Boolean care) {
        return ResponseDto.ok(adminService.readUsers(page, size, care));
    }

    /* 회원 상세 조회 */
    @GetMapping("/users/{userId}")
    public ResponseDto<?> readUserDetail(@PathVariable Long userId) {
        return ResponseDto.ok(adminService.readUserDetail(userId));
    }

    /* 회원 검색 */
    @GetMapping("/users/search")
    public ResponseDto<?> searchUsers(@RequestParam("text") String text) {
        return ResponseDto.ok(adminService.searchUsers(text));
    }

    /* 작성한 전체 후기 조회 */
    @GetMapping("/users/{userId}/reviews")
    public ResponseDto<?> readUserReviews(@PathVariable Long userId,
                                          @RequestParam(required = false, defaultValue = "0") int page,
                                          @RequestParam(required = false, defaultValue = "5") int size,
                                          @RequestParam(value = "hidden") Boolean hidden) {
        return ResponseDto.ok(adminService.readUserReviews(userId, page, size, hidden));
    }

    /* 후기 신고 목록 조회 */
    @GetMapping("/reports/reviews")
    public ResponseDto<?> readReviewReports(@RequestParam(required = false, defaultValue = "0") int page,
                                            @RequestParam(required = false, defaultValue = "19") int size,
                                            @RequestParam("isExec") Boolean isExec) {
        return ResponseDto.ok(adminService.readReviewReports(page, size, isExec));
    }

    /* 후기 신고 상세 조회 */
    @GetMapping("/reports/reviews/{reportId}")
    public ResponseDto<?> readReviewReportDetail(@PathVariable Long reportId) {
        return ResponseDto.ok(adminService.readReviewReportDetail(reportId));
    }

    /* 후기 신고 처리 생성 */
    @PostMapping("/reports/reviews/{reportId}")
    public ResponseDto<?> processReviewReport(@UserId Long adminId,
                                              @PathVariable Long reportId,
                                              @RequestBody CreateReportExecContentDto createReportExecContentDto) {
        adminService.processReviewReport(adminId, reportId, createReportExecContentDto);
        return ResponseDto.created("후기 신고 처리가 완료되었습니다.");
    }

    /* 후기 신고 처리 - 변경 사항 없음 */
    @PostMapping("/reports/reviews/{reportId}/exec")
    public ResponseDto<?> processReviewReportExec(@UserId Long adminId,
                                                  @PathVariable Long reportId) {
        adminService.processReviewReportExec(adminId, reportId);
        return ResponseDto.created("변경 사항 없이 처리되었습니다.");
    }

    /* 후기 신고 처리 내용 조회 */
    @GetMapping("/reports/reviews/{reportId}/exec")
    public ResponseDto<?> readReviewReportExecContent(@PathVariable Long reportId) {
        return ResponseDto.ok(adminService.readReviewReportExecContent(reportId));
    }

    /* 팝업 신고 목록 조회 */
    @GetMapping("/reports/popups")
    public ResponseDto<?> readPopupReports(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "19") int size,
                                           @RequestParam("isExec") Boolean isExec) {
        return ResponseDto.ok(adminService.readPopupReports(page, size, isExec));
    }

    /* 팝업 신고 상세 조회 */
    @GetMapping("/reports/popups/{reportId}")
    public ResponseDto<?> readPopupReportDetail(@PathVariable Long reportId) {
        return ResponseDto.ok(adminService.readPopupReportDetail(reportId));
    }

    /* 팝업 신고 처리 내용 조회 */
    @GetMapping("/reports/popups/{reportId}/exec")
    public ResponseDto<?> readPopupReportExecContent(@PathVariable Long reportId) {
        return ResponseDto.ok(adminService.readPopupReportExecContent(reportId));
    }

    /* 팝업 신고 처리 생성 */
    @PostMapping("/reports/popups/{reportId}")
    public ResponseDto<?> processPopupReport(@UserId Long adminId,
                                             @PathVariable Long reportId,
                                             @RequestBody CreateReportExecContentDto createReportExecContentDto) {
        adminService.processPopupReport(adminId, reportId, createReportExecContentDto);
        return ResponseDto.created("팝업 신고 처리가 완료되었습니다.");
    }

    @PostMapping(value = "/info/create", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createInformation(
            @UserId Long adminId,
            @RequestPart(value = "contents") InformAlarmCreateRequestDto requestDto,
            @RequestPart(value = "images") MultipartFile images
    ) {
        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }
        return ResponseDto.ok(adminService.createInformation(adminId, requestDto, images));
    }

}
