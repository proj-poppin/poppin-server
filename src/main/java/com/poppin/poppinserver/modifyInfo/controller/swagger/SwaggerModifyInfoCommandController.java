package com.poppin.poppinserver.modifyInfo.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.modifyInfo.dto.request.CreateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.request.UpdateModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.AdminModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.ModifyInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "정보 수정 요청", description = "정보 수정 요청 관리 API")
public interface SwaggerModifyInfoCommandController {

    @Operation(summary = "정보 수정 요청 생성", description = "사용자가 새로운 정보 수정 요청을 생성합니다.")
    @PostMapping(value = "", consumes = {"application/json", "multipart/form-data"})
    ResponseDto<ModifyInfoDto> createUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestParam(value = "popupId") String popupId,
            @RequestParam(value = "content") String content,
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "관리자 - 정보 수정 요청 임시 저장", description = "관리자가 정보 수정 요청을 임시 저장합니다.")
    @PutMapping(value = "/save", consumes = {"application/json", "multipart/form-data"})
    ResponseDto<AdminModifyInfoDto> saveModifyInfo(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") UpdateModifyInfoDto updateModifyInfoDto,
            @Parameter(hidden = true) Long adminId
    );

    @Operation(summary = "관리자 - 정보 수정 요청 최종 승인", description = "관리자가 정보 수정 요청을 최종 승인합니다.")
    @PutMapping(value = "", consumes = {"application/json", "multipart/form-data"})
    ResponseDto<AdminModifyInfoDto> modifyConfirm(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") UpdateModifyInfoDto updateModifyInfoDto,
            @Parameter(hidden = true) Long adminId
    );
}
