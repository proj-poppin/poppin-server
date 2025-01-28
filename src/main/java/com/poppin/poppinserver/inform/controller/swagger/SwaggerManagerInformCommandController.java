package com.poppin.poppinserver.inform.controller.swagger;

import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EAvailableAge;
import com.poppin.poppinserver.inform.dto.managerInform.request.CreateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.request.UpdateManagerInformDto;
import com.poppin.poppinserver.inform.dto.managerInform.response.ManagerInformDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Tag(name = "운영자 제보", description = "운영자 제보 관련 API")
public interface SwaggerManagerInformCommandController {

    @Operation(summary = "운영자 제보 생성", description = "팝업 운영자로부터 제보를 생성합니다.")
    ResponseDto<ManagerInformDto> createUserInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestParam("informerCompany") String informerCompany,
            @RequestParam("informerEmail") String informerEmail,
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
            HttpServletRequest request
    );

    @Operation(summary = "관리자 - 운영자 제보 임시저장", description = "운영자 제보를 임시로 저장합니다.")
    ResponseDto<ManagerInformDto> saveManagerInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") UpdateManagerInformDto updateManagerInformDto,
            @Parameter(hidden = true) Long adminId
    );

    @Operation(summary = "관리자 - 운영자 제보 업로드", description = "운영자 제보를 최종 업로드합니다.")
    ResponseDto<ManagerInformDto> uploadManagerInform(
            @RequestPart(value = "images") List<MultipartFile> images,
            @RequestPart(value = "contents") UpdateManagerInformDto updateManagerInformDto,
            @Parameter(hidden = true) Long adminId
    );
}
