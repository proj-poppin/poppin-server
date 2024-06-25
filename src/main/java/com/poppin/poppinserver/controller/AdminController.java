package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.alarm.request.InformAlarmRequestDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

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

//    @GetMapping("/users/special-care")
//    public ResponseDto<?> readSpecialCareUsers(@RequestParam(required = false, defaultValue = "0") int page,
//                                               @RequestParam(required = false, defaultValue = "44") int size) {
//        return ResponseDto.ok(adminService.readSpecialCareUsers(page, size));
//    }

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
    @GetMapping("/reports/reviews/{reportedReviewId}")
    public ResponseDto<?> readReviewReportDetail(@PathVariable Long reportedReviewId) {
        return ResponseDto.ok(adminService.readReviewReportDetail(reportedReviewId));
    }

    /* 후기 신고 처리 생성 */
    @PostMapping("/reports/reviews/{reportedReviewId}")
    public ResponseDto<?> processReviewReport(@UserId Long adminId,
                                             @PathVariable Long reportedReviewId,
                                             @RequestBody String content) {
        return ResponseDto.created(adminService.processReviewReport(adminId, reportedReviewId, content));
    }

    /* 팝업 신고 목록 조회 */
    @GetMapping("/reports/popups")
    public ResponseDto<?> readPopupReports(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "19") int size,
                                           @RequestParam("isExec") Boolean isExec) {
        return ResponseDto.ok(adminService.readPopupReports(page, size, isExec));
    }

    /* 팝업 신고 상세 조회 */
    @GetMapping("/reports/popups/{reportedPopupId}")
    public ResponseDto<?> readPopupReportDetail(@PathVariable Long reportedPopupId) {
        return ResponseDto.ok(adminService.readPopupReportDetail(reportedPopupId));
    }

    /* 팝업 신고 처리 생성 */
    @PostMapping("/reports/popups/{reportedPopupId}")
    public ResponseDto<?> processPopupReport(@UserId Long adminId,
                                             @PathVariable Long reportedPopupId,
                                             @RequestBody String content) {
        return ResponseDto.created(adminService.processPopupReport(adminId, reportedPopupId, content));
    }

    @PostMapping(value = "/info/create" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createInformation(
            @RequestPart(value = "images") MultipartFile images,
            @RequestPart(value = "contents") InformAlarmRequestDto requestDto,
            @UserId Long adminId
    ){
        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }
        return ResponseDto.ok(adminService.createInformation(images,requestDto,adminId));
    }

}
