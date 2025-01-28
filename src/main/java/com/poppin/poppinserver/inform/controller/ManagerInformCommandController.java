package com.poppin.poppinserver.inform.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.core.type.EAvailableAge;
import com.poppin.poppinserver.core.util.HeaderUtil;
import com.poppin.poppinserver.inform.controller.swagger.SwaggerManagerInformCommandController;
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.request.UpdateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import com.poppin.poppinserver.inform.service.AdminManagerInformService;
import com.poppin.poppinserver.inform.service.ManagerInformService;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import jakarta.servlet.http.HttpServletRequest;
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
public class ManagerInformCommandController implements SwaggerManagerInformCommandController {
    private final ManagerInformService managerInformService;
    private final AdminManagerInformService adminManagerInformService;

    private final HeaderUtil headerUtil;

    //운영자 제보 생성
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<ManagerInformDto> createUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestParam("informerCompany") @NotNull String informerCompany,
            @RequestParam("informerEmail") @NotNull String informerEmail,
            @RequestParam(value = "storeUrl", required = false) String storeUrl,
            @RequestParam(value = "storeName", required = false) String storeName,
            @RequestParam(value = "storeBriefDescription", required = false) String storeBriefDescription,
            @RequestParam(value = "storeAddress", required = false) String storeAddress,
            @RequestParam(value = "storeDetailAddress", required = false) String storeDetailAddress,
            @RequestParam(value = "isEntranceFeeRequired", required = false) Boolean isEntranceFeeRequired,
            @RequestParam(value = "entranceFee", required = false) String entranceFee,
            @RequestParam(value = "availableAge", required = false) EAvailableAge availableAge,
            @RequestParam(value = "parkingAvailable", required = false) Boolean parkingAvailable,
            @RequestParam(value = "isReservationRequired", required = false) Boolean isReservationRequired,
            @RequestParam(value = "openDate", required = false) LocalDate openDate,
            @RequestParam(value = "closeDate", required = false) LocalDate closeDate,
            @RequestParam(value = "openTime", required = false) LocalTime openTime,
            @RequestParam(value = "closeTime", required = false) LocalTime closeTime,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "operationException", required = false) String operationException,
            @RequestParam("filteringThreeCategories") String filteringThreeCategories,
            @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
            HttpServletRequest request) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        CreateManagerInformDto createManagerInformDto = new CreateManagerInformDto(informerCompany, informerEmail,
                storeUrl, storeName, storeBriefDescription, storeAddress, storeDetailAddress, isEntranceFeeRequired, entranceFee, availableAge,
                parkingAvailable, isReservationRequired, openDate, closeDate, openTime, closeTime, latitude, longitude, operationException);

        Long userId = headerUtil.parseUserId(request);

        if (userId == null) {
            return ResponseDto.ok(managerInformService.createGuestManagerInform(createManagerInformDto, filteringThreeCategories, filteringFourteenCategories,images));
        } else {
            return ResponseDto.ok(managerInformService.createManagerInform(createManagerInformDto, filteringThreeCategories, filteringFourteenCategories, images, userId));
        }
    } // 운영자 제보 생성

    // 운영자 제보 임시저장
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<ManagerInformDto> saveManagerInform(@RequestPart(value = "images") List<MultipartFile> images,
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
    public ResponseDto<ManagerInformDto> uploadManagerInform(@RequestPart(value = "images") List<MultipartFile> images,
                                              @RequestPart(value = "contents") @Valid UpdateManagerInformDto updateManagerInformDto,
                                              @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(adminManagerInformService.uploadPopup(updateManagerInformDto, images, adminId));
    }
}
