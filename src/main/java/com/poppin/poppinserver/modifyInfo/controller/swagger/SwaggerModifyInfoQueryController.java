package com.poppin.poppinserver.modifyInfo.controller.swagger;

import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.modifyInfo.dto.response.AdminModifyInfoDto;
import com.poppin.poppinserver.modifyInfo.dto.response.ModifyInfoSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "정보 수정 요청 조회", description = "정보 수정 요청 조회 관련 API")
public interface SwaggerModifyInfoQueryController {

    @Operation(summary = "관리자 - 정보 수정 요청 조회", description = "특정 정보 수정 요청을 조회합니다.")
    @GetMapping("")
    ResponseDto<AdminModifyInfoDto> readModifyInfo(
            @RequestParam("infoId") Long modifyInfoId,
            @Parameter(hidden = true) Long adminId
    );

    @Operation(summary = "관리자 - 정보 수정 요청 목록 조회", description = "정보 수정 요청 목록을 조회합니다.")
    @GetMapping("/list")
    ResponseDto<PagingResponseDto<List<ModifyInfoSummaryDto>>> readModifyInfoList(
            @RequestParam("isExec") Boolean isExec,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
