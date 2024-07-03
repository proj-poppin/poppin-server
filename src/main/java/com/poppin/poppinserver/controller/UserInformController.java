package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.userInform.request.UpdateUserInfromDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.service.UserInformService;
import com.poppin.poppinserver.type.EInformProgress;
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
@RequestMapping("/api/v1/user-inform")
public class UserInformController {
    private final UserInformService userInformService;

    //사용자 제보 생성
    @PostMapping(value = "/report", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestParam(value = "name") String name,
                                           @RequestParam(value = "contactLink", required = false) String contactLink,
                                           @RequestParam(value = "fashionBeauty") Boolean fashionBeauty,
                                           @RequestParam(value = "characters") Boolean characters,
                                           @RequestParam(value = "foodBeverage") Boolean foodBeverage,
                                           @RequestParam(value = "webtoonAni") Boolean webtoonAni,
                                           @RequestParam(value = "interiorThings") Boolean interiorThings,
                                           @RequestParam(value = "movie") Boolean movie,
                                           @RequestParam(value = "musical") Boolean musical,
                                           @RequestParam(value = "sports") Boolean sports,
                                           @RequestParam(value = "game") Boolean game,
                                           @RequestParam(value = "itTech") Boolean itTech,
                                           @RequestParam(value = "kpop") Boolean kpop,
                                           @RequestParam(value = "alcohol") Boolean alcohol,
                                           @RequestParam(value = "animalPlant") Boolean animalPlant,
                                           @RequestParam(value = "etc") Boolean etc,
                                           @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(userInformService.createUserInform(name, contactLink, fashionBeauty, characters, foodBeverage, webtoonAni, interiorThings, movie,
                musical, sports, game, itTech, kpop, alcohol, animalPlant, etc, images, userId));
    } // 제보 생성

    // 비로그인 사용자 제보 생성
    @PostMapping(value = "/guest/report", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createGurestUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestParam(value = "name") String name,
                                           @RequestParam(value = "contactLink", required = false) String contactLink,
                                           @RequestParam(value = "fashionBeauty") Boolean fashionBeauty,
                                           @RequestParam(value = "characters") Boolean characters,
                                           @RequestParam(value = "foodBeverage") Boolean foodBeverage,
                                           @RequestParam(value = "webtoonAni") Boolean webtoonAni,
                                           @RequestParam(value = "interiorThings") Boolean interiorThings,
                                           @RequestParam(value = "movie") Boolean movie,
                                           @RequestParam(value = "musical") Boolean musical,
                                           @RequestParam(value = "sports") Boolean sports,
                                           @RequestParam(value = "game") Boolean game,
                                           @RequestParam(value = "itTech") Boolean itTech,
                                           @RequestParam(value = "kpop") Boolean kpop,
                                           @RequestParam(value = "alcohol") Boolean alcohol,
                                           @RequestParam(value = "animalPlant") Boolean animalPlant,
                                           @RequestParam(value = "etc") Boolean etc) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(userInformService.createGuestUserInform(name, contactLink, fashionBeauty, characters, foodBeverage, webtoonAni, interiorThings, movie,
                musical, sports, game, itTech, kpop, alcohol, animalPlant, etc, images));
    } // 제보 생성

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseDto<?> readUserInform(@RequestParam("informId") Long userInformId) {
        return ResponseDto.ok(userInformService.readUserInform(userInformId));
    } // 제보 조회

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> saveUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestPart(value = "contents") @Valid UpdateUserInfromDto updateUserInfromDto,
                                           @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(userInformService.updateUserInform(updateUserInfromDto, images, adminId));
    } // 제보 임시 저장

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> uploadUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                         @RequestPart(value = "contents") @Valid UpdateUserInfromDto updateUserInfromDto,
                                         @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(userInformService.uploadPopup(updateUserInfromDto, images, adminId));
    } // 제보 업로드 승인

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseDto<?> readUserInformList(@RequestParam(value = "page") int page,
                                             @RequestParam(value = "size") int size,
                                             @RequestParam(value = "prog") EInformProgress progress){
        return ResponseDto.ok(userInformService.reatUserInformList(page, size, progress));
    } // 제보 목록 조회
}
