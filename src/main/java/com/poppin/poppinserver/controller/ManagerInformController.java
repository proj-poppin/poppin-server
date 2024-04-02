package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.dto.userInform.request.CreateUserInformDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.service.ManagerInformService;
import com.poppin.poppinserver.service.UserInformService;
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
@RequestMapping("/api/v1/manager-inform")
public class ManagerInformController {
    private final ManagerInformService managerInformService;

    //운영자 제보 생성
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestPart(value = "contents") @Valid CreateManagerInformDto createManagerInformDto,
                                           @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(managerInformService.createManagerInform(createManagerInformDto, images, userId));
    }

    @GetMapping("") // 운영자 제보 조회
    public ResponseDto<?> readUserInform(@RequestParam("manageInformId") Long manageInformId) {
        return ResponseDto.ok(managerInformService.readManageInform(manageInformId));
    }
}
