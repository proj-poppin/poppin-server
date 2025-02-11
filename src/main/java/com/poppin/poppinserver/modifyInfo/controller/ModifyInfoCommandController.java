package com.poppin.poppinserver.modifyInfo.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.modifyInfo.controller.swagger.SwaggerModifyInfoCommandController;
import com.poppin.poppinserver.modifyInfo.dto.request.CreateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.request.UpdateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.AdminModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.ModifyInfoDto;
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
public class ModifyInfoCommandController implements SwaggerModifyInfoCommandController {
    private final ModifyInfoService modifyInfoService;
    private final AdminModifyInfoService adminModifyInfoService;

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<ModifyInfoDto> createUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                                       @RequestPart(value = "contents") @Valid CreateModifyInfoDto createModifyInfoDto,
                                                       @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(modifyInfoService.createModifyInfo(createModifyInfoDto, images, userId));
    } // 요청 생성

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<AdminModifyInfoDto> saveModifyInfo(@RequestPart(value = "images") List<MultipartFile> images,
                                                          @RequestPart(value = "contents") @Valid UpdateModifyInfoDto updateModifyInfoDto,
                                                          @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminModifyInfoService.updateModifyInfo(updateModifyInfoDto, images, adminId));
    } // 제보 임시 저장

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<AdminModifyInfoDto> modifyConfirm(@RequestPart(value = "images") List<MultipartFile> images,
                                        @RequestPart(value = "contents") @Valid UpdateModifyInfoDto updateModifyInfoDto,
                                        @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminModifyInfoService.uploadModifyInfo(updateModifyInfoDto, images, adminId));
    } // 최종 수정 승인
}
