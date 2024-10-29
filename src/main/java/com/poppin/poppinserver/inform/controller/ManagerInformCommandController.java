package com.poppin.poppinserver.inform.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EAvailableAge;
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.request.UpdateManagerInformDto;
import com.poppin.poppinserver.inform.service.AdminManagerInformService;
import com.poppin.poppinserver.inform.service.ManagerInformService;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/manager-inform")
public class ManagerInformCommandController {
    private final ManagerInformService managerInformService;
    private final AdminManagerInformService adminManagerInformService;

    //운영자 제보 생성
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestParam("affiliation") @NotNull String affiliation,
            @RequestParam("informerEmail") @NotNull String informerEmail,
            @RequestParam(value = "homepageLink", required = false) String homepageLink,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "introduce", required = false) String introduce,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "addressDetail", required = false) String addressDetail,
            @RequestParam(value = "entranceRequired", required = false) Boolean entranceRequired,
            @RequestParam(value = "entranceFee", required = false) String entranceFee,
            @RequestParam(value = "availableAge", required = false) EAvailableAge availableAge,
            @RequestParam(value = "parkingAvailable", required = false) Boolean parkingAvailable,
            @RequestParam(value = "resvRequired", required = false) Boolean resvRequired,
            @RequestParam(value = "openDate", required = false) LocalDate openDate,
            @RequestParam(value = "closeDate", required = false) LocalDate closeDate,
            @RequestParam(value = "openTime", required = false) LocalTime openTime,
            @RequestParam(value = "closeTime", required = false) LocalTime closeTime,
            @RequestParam(value = "operationExcept", required = false) String operationExcept,
            @RequestParam("market") @NotNull Boolean market,
            @RequestParam("display") @NotNull Boolean display,
            @RequestParam("experience") @NotNull Boolean experience,
            @RequestParam("fashionBeauty") @NotNull Boolean fashionBeauty,
            @RequestParam("characters") @NotNull Boolean characters,
            @RequestParam("foodBeverage") @NotNull Boolean foodBeverage,
            @RequestParam("webtoonAni") @NotNull Boolean webtoonAni,
            @RequestParam("interiorThings") @NotNull Boolean interiorThings,
            @RequestParam("movie") @NotNull Boolean movie,
            @RequestParam("musical") @NotNull Boolean musical,
            @RequestParam("sports") @NotNull Boolean sports,
            @RequestParam("game") @NotNull Boolean game,
            @RequestParam("itTech") @NotNull Boolean itTech,
            @RequestParam("kpop") @NotNull Boolean kpop,
            @RequestParam("alcohol") @NotNull Boolean alcohol,
            @RequestParam("animalPlant") @NotNull Boolean animalPlant,
            @RequestParam(value = "etc", required = false) Boolean etc,
            @UserId Long userId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        CreatePreferedDto prefered = new CreatePreferedDto(market, display, experience, null);
        CreateTasteDto taste = new CreateTasteDto(fashionBeauty, characters, foodBeverage, webtoonAni, interiorThings,
                movie, musical, sports, game, itTech, kpop, alcohol, animalPlant, etc);
        CreateManagerInformDto createManagerInformDto = new CreateManagerInformDto(affiliation, informerEmail,
                homepageLink, name, introduce, address, addressDetail, entranceRequired, entranceFee, availableAge,
                parkingAvailable, resvRequired, openDate, closeDate, openTime, closeTime, operationExcept, prefered,
                taste);

        return ResponseDto.ok(managerInformService.createManagerInform(createManagerInformDto, images, userId));
    } // 운영자 제보 생성

    // 비로그인 운영자 제보 생성
    @PostMapping(value = "/guest", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createGuestUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestParam("affiliation") @NotNull String affiliation,
            @RequestParam("informerEmail") @NotNull String informerEmail,
            @RequestParam(value = "homepageLink", required = false) String homepageLink,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "introduce", required = false) String introduce,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "addressDetail", required = false) String addressDetail,
            @RequestParam(value = "entranceRequired", required = false) Boolean entranceRequired,
            @RequestParam(value = "entranceFee", required = false) String entranceFee,
            @RequestParam(value = "availableAge", required = false) EAvailableAge availableAge,
            @RequestParam(value = "parkingAvailable", required = false) Boolean parkingAvailable,
            @RequestParam(value = "resvRequired", required = false) Boolean resvRequired,
            @RequestParam(value = "openDate", required = false) LocalDate openDate,
            @RequestParam(value = "closeDate", required = false) LocalDate closeDate,
            @RequestParam(value = "openTime", required = false) LocalTime openTime,
            @RequestParam(value = "closeTime", required = false) LocalTime closeTime,
            @RequestParam(value = "operationExcept", required = false) String operationExcept,
            @RequestParam("market") @NotNull Boolean market,
            @RequestParam("display") @NotNull Boolean display,
            @RequestParam("experience") @NotNull Boolean experience,
            @RequestParam("fashionBeauty") @NotNull Boolean fashionBeauty,
            @RequestParam("characters") @NotNull Boolean characters,
            @RequestParam("foodBeverage") @NotNull Boolean foodBeverage,
            @RequestParam("webtoonAni") @NotNull Boolean webtoonAni,
            @RequestParam("interiorThings") @NotNull Boolean interiorThings,
            @RequestParam("movie") @NotNull Boolean movie,
            @RequestParam("musical") @NotNull Boolean musical,
            @RequestParam("sports") @NotNull Boolean sports,
            @RequestParam("game") @NotNull Boolean game,
            @RequestParam("itTech") @NotNull Boolean itTech,
            @RequestParam("kpop") @NotNull Boolean kpop,
            @RequestParam("alcohol") @NotNull Boolean alcohol,
            @RequestParam("animalPlant") @NotNull Boolean animalPlant,
            @RequestParam(value = "etc", required = false) Boolean etc) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        CreatePreferedDto prefered = new CreatePreferedDto(market, display, experience, null);
        CreateTasteDto taste = new CreateTasteDto(fashionBeauty, characters, foodBeverage, webtoonAni, interiorThings,
                movie, musical, sports, game, itTech, kpop, alcohol, animalPlant, etc);
        CreateManagerInformDto createManagerInformDto = new CreateManagerInformDto(affiliation, informerEmail,
                homepageLink, name, introduce, address, addressDetail, entranceRequired, entranceFee, availableAge,
                parkingAvailable, resvRequired, openDate, closeDate, openTime, closeTime, operationExcept, prefered,
                taste);

        return ResponseDto.ok(managerInformService.createGuestManagerInform(createManagerInformDto, images));
    } // 운영자 제보 생성

    // 운영자 제보 임시저장
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> saveManagerInform(@RequestPart(value = "images") List<MultipartFile> images,
                                            @RequestPart(value = "contents") @Valid UpdateManagerInformDto updateManagerInformDto,
                                            @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminManagerInformService.updateManageInform(updateManagerInformDto, images, adminId));
    }

    //운영자 제보 업로드
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> uploadManagerInform(@RequestPart(value = "images") List<MultipartFile> images,
                                              @RequestPart(value = "contents") @Valid UpdateManagerInformDto updateManagerInformDto,
                                              @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminManagerInformService.uploadPopup(updateManagerInformDto, images, adminId));
    }
}
