package com.poppin.poppinserver.modifyInfo.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.modifyInfo.dto.request.CreateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.request.UpdateModifyInfoDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.modifyInfo.service.AdminModifyInfoService;
import com.poppin.poppinserver.modifyInfo.service.ModifyInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/modify-info") // 정보수정요청
public class ModifyInfoController {
    private final ModifyInfoService modifyInfoService;
    private final AdminModifyInfoService adminModifyInfoService;

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestParam(value = "popupId") String popupId,
                                           @RequestParam(value = "content") String content,
                                           @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        CreateModifyInfoDto createModifyInfoDto = new CreateModifyInfoDto(Long.valueOf(popupId), content);

        return ResponseDto.ok(modifyInfoService.createModifyInfo(createModifyInfoDto, images, userId));
    } // 요청 생성

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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> saveModifyInfo(@RequestPart(value = "images") List<MultipartFile> images,
                                         @RequestPart(value = "contents") @Valid UpdateModifyInfoDto updateModifyInfoDto,
                                         @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminModifyInfoService.updateModifyInfo(updateModifyInfoDto, images, adminId));
    } // 제보 임시 저장

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> modifyConfirm(@RequestPart(value = "images") List<MultipartFile> images,
                                        @RequestPart(value = "contents") @Valid UpdateModifyInfoDto updateModifyInfoDto,
                                        @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminModifyInfoService.uploadModifyInfo(updateModifyInfoDto, images, adminId));
    } // 최종 수정 승인
}
