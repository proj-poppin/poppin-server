package com.poppin.poppinserver.modifyInfo.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.modifyInfo.service.AdminModifyInfoService;
import com.poppin.poppinserver.modifyInfo.service.ModifyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/modify-info") // 정보수정요청
public class ModifyInfoQueryController {
    private final AdminModifyInfoService adminModifyInfoService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseDto<?> readModifyInfo(@RequestParam("infoId") Long modifyInfoId,
                                         @UserId Long adminId) {
        return ResponseDto.ok(adminModifyInfoService.readModifyInfo(modifyInfoId, adminId));
    } // 요청 조회

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseDto<?> readModifyInfoList(@RequestParam("isExec") Boolean isExec,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        return ResponseDto.ok(adminModifyInfoService.readModifyInfoList(page, size, isExec));
    } // 목록 조회
}
