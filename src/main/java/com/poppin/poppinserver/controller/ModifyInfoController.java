package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.dto.modifyInfo.request.CreateModifyInfoDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.service.ModifyInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/modify-info") // 정보수정요청
public class ModifyInfoController {
    private final ModifyInfoService modifyInfoService;

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestPart(value = "contents") @Valid CreateModifyInfoDto createModifyInfoDto,
                                           @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(modifyInfoService.createModifyInfo(createModifyInfoDto, images, userId));
    } // 요청 생성

    @GetMapping("")
    public ResponseDto<?> readModifyInfo(@RequestParam("infoId") Long modifyInfoId){
        return ResponseDto.ok(modifyInfoService.readModifyInfo(modifyInfoId));
    } // 요청 조회

    @GetMapping ("/list")
    public ResponseDto<?> readModifyInfoList(){
        return ResponseDto.ok(modifyInfoService.readModifyInfoList());
    } // 목록 조회
}
