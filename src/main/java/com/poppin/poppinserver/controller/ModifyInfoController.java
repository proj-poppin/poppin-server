package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.ModifyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/modify-info") // 정보수정요청
public class ModifyInfoController {
    private final ModifyInfoService modifyInfoService;


    @GetMapping ("/list") // 목록 조회
    public ResponseDto<?> readModifyInfoList(){
        return ResponseDto.ok(modifyInfoService.readModifyInfoList());
    }
}
