package com.poppin.poppinserver.popup.controller.swagger;

import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.popup.dto.popup.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "팝업 조회", description = "팝업 조회 관련 API")
public interface SwaggerPopupQueryController {

    @Operation(summary = "관리자 - 팝업 조회", description = "관리자가 특정 팝업을 조회합니다.")
    @GetMapping("/admin")
    ResponseDto<AdminPopupDto> readPopup(@RequestParam("id") Long popupId);

    @Operation(summary = "관리자 - 전체 팝업 리스트 조회", description = "관리자가 전체 팝업 리스트를 조회합니다.")
    @GetMapping("/admin/list")
    ResponseDto<PagingResponseDto<ManageListDto>> readManageList(
            @RequestParam("oper") EOperationStatus oper,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @Parameter(hidden = true) Long adminId
    );

    @Operation(summary = "관리자 - 전체 팝업 검색", description = "관리자가 전체 팝업을 검색합니다.")
    @GetMapping("/admin/search")
    ResponseDto<PagingResponseDto<ManageListDto>> readManageList(
            @RequestParam("text") String text,
            @RequestParam("oper") EOperationStatus oper,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    @Operation(summary = "인기 팝업 목록 조회", description = "인기 팝업 목록을 조회합니다.")
    @GetMapping("/hot-list")
    ResponseDto<List<PopupSummaryDto>> readHotList();

    @Operation(summary = "새로 오픈 팝업 목록 조회", description = "새로 오픈한 팝업 목록을 조회합니다.")
    @GetMapping("/new-list")
    ResponseDto<List<PopupSummaryDto>> readNewList();

    @Operation(summary = "종료 임박 팝업 목록 조회", description = "종료가 임박한 팝업 목록을 조회합니다.")
    @GetMapping("/closing-list")
    ResponseDto<List<PopupSummaryDto>> readclosingList();

    @Operation(summary = "관심 팝업 목록 조회", description = "사용자가 등록한 관심 팝업 목록을 조회합니다.")
    @GetMapping("/interested-list")
    ResponseDto<List<PopupStoreDto>> readInterestedList(
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "팝업 검색", description = "사용자가 팝업을 검색합니다.")
    @GetMapping("/search")
    ResponseDto<PagingResponseDto<List<PopupStoreDto>>> readSearchList(
            @RequestParam("searchName") String searchName,
            @Parameter(example = "market,display,experience") @RequestParam("filteringThreeCategories") String filteringThreeCategories,
            @Parameter(example = "fashionBeauty,characters,foodBeverage,webtoonAni,interiorThings,movie,musical,sports,game,itTech,kpop,alcohol,animalPlant,etc") @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
            @RequestParam("operationStatus") EOperationStatus oper,
            @RequestParam("order") EPopupSort order,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            HttpServletRequest request
    );

    @Operation(summary = "취향 저격 팝업 목록 조회", description = "사용자의 취향에 맞는 팝업 목록을 조회합니다.")
    @GetMapping("/taste-list")
    ResponseDto<PopupTasteDto> readTasteList(
            @Parameter(hidden = true) Long userId
    );

    @Operation(summary = "팝업 상세 조회", description = "특정 팝업의 상세 정보를 조회합니다.")
    @GetMapping("/detail/{popupId}")
    ResponseDto<PopupStoreDto> readPopup(
            @PathVariable String popupId,
            HttpServletRequest request
    );

    @Operation(summary = "방문한 팝업 목록 조회", description = "사용자가 방문한 팝업 목록을 조회합니다.")
    @GetMapping("/visited")
    ResponseDto<List<VisitedPopupDto>> getVisitedPopupList(
            @Parameter(hidden = true) Long userId
    );
}

