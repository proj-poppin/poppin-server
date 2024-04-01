package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.userInform.request.CreateUserInformDto;
import com.poppin.poppinserver.dto.userInform.request.UpdateUserInfromDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
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
@RequestMapping("/api/v1/user-inform")
public class UserInformController {
    private final UserInformService userInformService;

    //사용자 제보 생성
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestPart(value = "contents") @Valid CreateUserInformDto createUserInformDto,
                                           @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(userInformService.createUserInform(createUserInformDto, images, userId));
    }

    @GetMapping("")
    public ResponseDto<?> readUserInform(@RequestParam("userInformId") Long userInformId) {
        return ResponseDto.ok(userInformService.readUserInform(userInformId));
    }

    @PutMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> saveUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestPart(value = "contents") @Valid UpdateUserInfromDto updateUserInfromDto,
                                           @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(userInformService.updateUserInform(updateUserInfromDto, images));
    }
}
