package com.poppin.poppinserver.inform.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.inform.dto.userInform.request.UpdateUserInformDto;
import com.poppin.poppinserver.inform.service.AdminUserInformService;
import com.poppin.poppinserver.inform.service.UserInformService;
import jakarta.servlet.http.HttpServletRequest;
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
public class UserInformCommandController {
    private final UserInformService userInformService;
    private final AdminUserInformService adminUserInformService;

    private final HeaderUtil headerUtil;

    //사용자 제보 생성
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestParam(value = "storeName") String storeName,
                                           @RequestParam(value = "contactLink", required = false) String contactLink,
                                           @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
                                           HttpServletRequest request) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        Long userId = headerUtil.parseUserId(request);

        if (userId == null) {
            return ResponseDto.ok(
                    userInformService.createGuestUserInform(storeName, contactLink, filteringFourteenCategories, images));
        } else {
            return ResponseDto.ok(
                    userInformService.createUserInform(storeName, contactLink, filteringFourteenCategories, images, userId));
        }
    } // 제보 생성

//    // 비로그인 사용자 제보 생성
//    @PostMapping(value = "/guest/report", consumes = {MediaType.APPLICATION_JSON_VALUE,
//            MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseDto<?> createGurestUserInform(@RequestPart(value = "images") List<MultipartFile> images,
//                                                 @RequestParam(value = "name") String name,
//                                                 @RequestParam(value = "contactLink", required = false) String contactLink,
//                                                 @RequestParam(value = "fashionBeauty") Boolean fashionBeauty,
//                                                 @RequestParam(value = "characters") Boolean characters,
//                                                 @RequestParam(value = "foodBeverage") Boolean foodBeverage,
//                                                 @RequestParam(value = "webtoonAni") Boolean webtoonAni,
//                                                 @RequestParam(value = "interiorThings") Boolean interiorThings,
//                                                 @RequestParam(value = "movie") Boolean movie,
//                                                 @RequestParam(value = "musical") Boolean musical,
//                                                 @RequestParam(value = "sports") Boolean sports,
//                                                 @RequestParam(value = "game") Boolean game,
//                                                 @RequestParam(value = "itTech") Boolean itTech,
//                                                 @RequestParam(value = "kpop") Boolean kpop,
//                                                 @RequestParam(value = "alcohol") Boolean alcohol,
//                                                 @RequestParam(value = "animalPlant") Boolean animalPlant,
//                                                 @RequestParam(value = "etc") Boolean etc) {
//
//        if (images.isEmpty()) {
//            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
//        }
//
//        return ResponseDto.ok(
//                userInformService.createGuestUserInform(name, contactLink, fashionBeauty, characters, foodBeverage,
//                        webtoonAni, interiorThings, movie,
//                        musical, sports, game, itTech, kpop, alcohol, animalPlant, etc, images));
//    } // 제보 생성

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> saveUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                         @RequestPart(value = "contents") @Valid UpdateUserInformDto updateUserInformDto,
                                         @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminUserInformService.updateUserInform(updateUserInformDto, images, adminId));
    } // 제보 임시 저장

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> uploadUserInform(@RequestPart(value = "images") List<MultipartFile> images,
                                           @RequestPart(value = "contents") @Valid UpdateUserInformDto updateUserInformDto,
                                           @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminUserInformService.uploadPopup(updateUserInformDto, images, adminId));
    } // 제보 업로드 승인
}
