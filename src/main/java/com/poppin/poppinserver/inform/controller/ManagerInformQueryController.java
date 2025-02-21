package com.poppin.poppinserver.inform.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.controller.swagger.SwaggerManagerInformQueryController;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformSummaryDto;
import com.poppin.poppinserver.inform.service.AdminManagerInformService;
import com.poppin.poppinserver.inform.service.ManagerInformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/manager-inform")
public class ManagerInformQueryController implements SwaggerManagerInformQueryController {
    private final AdminManagerInformService adminManagerInformService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("") // 운영자 제보 조회
    public ResponseDto<ManagerInformDto> readUserInform(@RequestParam("informId") Long managerInformId) {
        return ResponseDto.ok(adminManagerInformService.readManageInform(managerInformId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list") // 운영자 제보 목록 조회
    public ResponseDto<PagingResponseDto<List<ManagerInformSummaryDto>>> readManagerInformList(@RequestParam(value = "page") int page,
                                                                                               @RequestParam(value = "size") int size,
                                                                                               @RequestParam(value = "prog") EInformProgress progress
    ) {
        return ResponseDto.ok(adminManagerInformService.readManagerInformList(page, size, progress));
    }
}
