package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.faq.request.FaqRequestDto;
import com.poppin.poppinserver.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
                                    @RequestParam(required = false, defaultValue = "44") int size) {
        return ResponseDto.ok(adminService.readUsers(page, size));
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

    @GetMapping("/users/special-care")
    public ResponseDto<?> readSpecialCareUsers(@RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "44") int size) {
        return ResponseDto.ok(adminService.readSpecialCareUsers(page, size));
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
    @GetMapping("/reports/reviews/{reviewId}")
    public ResponseDto<?> readReviewReportDetail(@PathVariable Long reviewId) {
        return ResponseDto.ok(adminService.readReviewReportDetail(reviewId));
    }

    /* 후기 신고 처리 생성 */
    @PostMapping("/reports/reviews/{reviewId}")
    public ResponseDto<?> processReviewReport(@UserId Long adminId,
                                             @PathVariable Long reviewId,
                                             @RequestBody String content) {
        return ResponseDto.created(adminService.processReviewReport(adminId, reviewId, content));
    }

    /* 팝업 신고 목록 조회 */
    @GetMapping("/reports/popups")
    public ResponseDto<?> readPopupReports(@RequestParam(required = false, defaultValue = "0") int page,
                                           @RequestParam(required = false, defaultValue = "19") int size,
                                           @RequestParam("isExec") Boolean isExec) {
        return ResponseDto.ok(adminService.readPopupReports(page, size, isExec));
    }

    /* 팝업 신고 상세 조회 */
    @GetMapping("/reports/popups/{popupId}")
    public ResponseDto<?> readPopupReportDetail(@PathVariable Long popupId) {
        return ResponseDto.ok(adminService.readPopupReportDetail(popupId));
    }

    /* 팝업 신고 처리 생성 */
    @PostMapping("/reports/popups/{popupId}")
    public ResponseDto<?> processPopupReport(@UserId Long adminId,
                                             @PathVariable Long popupId,
                                             @RequestBody String content) {
        return ResponseDto.created(adminService.processPopupReport(adminId, popupId, content));
    }


}
