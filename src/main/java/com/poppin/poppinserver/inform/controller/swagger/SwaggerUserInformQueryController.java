package com.poppin.poppinserver.inform.controller.swagger;

import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.inform.dto.userInform.response.UserInformSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "사용자 제보 조회", description = "사용자 제보 조회 관련 API")
public interface SwaggerUserInformQueryController {

    @Operation(summary = "관리자 - 사용자 제보 조회", description = "특정 사용자 제보를 조회합니다.")
    @GetMapping("")
    ResponseDto<UserInformDto> readUserInform(@RequestParam("informId") Long userInformId);

    @Operation(summary = "관리자 - 사용자 제보 목록 조회", description = "사용자 제보의 목록을 조회합니다.")
    @GetMapping("/list")
    ResponseDto<PagingResponseDto<List<UserInformSummaryDto>>> readUserInformList(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "prog") EInformProgress progress,
            Long adminId
    );
}
