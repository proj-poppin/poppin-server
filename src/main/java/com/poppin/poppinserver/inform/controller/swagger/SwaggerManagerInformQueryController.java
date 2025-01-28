package com.poppin.poppinserver.inform.controller.swagger;

import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "운영자 제보 조회", description = "운영자 제보 조회 관련 API")
public interface SwaggerManagerInformQueryController {

    @Operation(summary = "관리자 - 운영자 제보 조회", description = "특정 팝업 운영자 제보를 조회합니다.")
    @GetMapping("")
    ResponseDto<ManagerInformDto> readUserInform(@RequestParam("informId") Long managerInformId);

    @Operation(summary = "관리자 - 운영자 제보 목록 조회", description = "운영자 제보의 목록을 조회합니다.")
    @GetMapping("/list")
    ResponseDto<PagingResponseDto<List<ManagerInformSummaryDto>>> readManagerInformList(@RequestParam(value = "page") int page,
                                                                                        @RequestParam(value = "size") int size,
                                                                                        @RequestParam(value = "prog") EInformProgress progress);
}
