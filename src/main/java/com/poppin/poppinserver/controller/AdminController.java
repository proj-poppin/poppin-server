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
        return ResponseDto.ok(adminService.createFAQ(adminId, faqRequestDto));
    }

    /* FAQ 삭제 */
    @DeleteMapping("/support/faqs/{faqId}")
    public ResponseDto<?> deleteFaq(@PathVariable Long faqId) {
        adminService.deleteFAQ(faqId);
        return ResponseDto.ok("FAQ가 삭제되었습니다.");
    }

    /* 회원 관리 목록 조회 */
    @GetMapping("/users")
    public ResponseDto<?> readUsers(@RequestParam(required = false, defaultValue = "nickname") String sortField,
                                    @RequestParam(required = false, defaultValue = "asc") String sortOrder) {
        return ResponseDto.ok(adminService.readUsers(sortField, sortOrder));
    }

    /* 회원 상세 조회 */
    /* 작성한 전체 후기 조회 */
    @GetMapping("/users/{userId}")
    public ResponseDto<?> readUserDetail(@PathVariable Long userId) {
        return ResponseDto.ok(adminService.readUserDetail(userId));
    }

    /* 회원 검색 */
    @GetMapping("/users/search")
    public ResponseDto<?> searchUsers(@RequestParam("text") String text) {
        return ResponseDto.ok(adminService.searchUsers(text));
    }

}
