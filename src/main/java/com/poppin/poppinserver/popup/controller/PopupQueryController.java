package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.admin.service.AdminPopupService;
import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.PagingResponseDto;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.popup.controller.swagger.SwaggerPopupQueryController;
import com.poppin.poppinserver.popup.dto.popup.response.*;
import com.poppin.poppinserver.popup.service.ListingPopupService;
import com.poppin.poppinserver.popup.service.PopupService;
import com.poppin.poppinserver.popup.service.SearchPopupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

//팝업 조회
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup")
public class PopupQueryController implements SwaggerPopupQueryController {
    private final PopupService popupService;
    private final ListingPopupService listingPopupService;
    private final SearchPopupService searchPopupService;

    private final AdminPopupService adminPopupService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseDto<AdminPopupDto> readPopup(@RequestParam("id") Long popupId) {
        log.info(LocalDateTime.now().toString());
        return ResponseDto.ok(adminPopupService.readPopup(popupId));
    } // 전체팝업관리 - 팝업조회

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/list")
    public ResponseDto<PagingResponseDto<ManageListDto>> readManageList(@RequestParam("oper") EOperationStatus oper,
                                                                        @RequestParam("page") int page,
                                                                        @RequestParam("size") int size,
                                                                        @UserId Long adminId) {
        return ResponseDto.ok(adminPopupService.readManageList(adminId, oper, page, size));
    } // 전체팝업관리 - 전체 팝업 리스트 조회


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search")
    public ResponseDto<PagingResponseDto<ManageListDto>> readManageList(@RequestParam("text") String text,
                                                                        @RequestParam("oper") EOperationStatus oper,
                                                                        @RequestParam("page") int page,
                                                                        @RequestParam("size") int size) {
        return ResponseDto.ok(adminPopupService.searchManageList(text, page, size, oper));
    } // 전체팝업관리 - 전체 팝업 검색

    @GetMapping("/hot-list") // 인기 팝업 목록 조회
    public ResponseDto<List<PopupSummaryDto>> readHotList(HttpServletRequest request) {
        return ResponseDto.ok(listingPopupService.readHotList(request));
    }

    @GetMapping("/new-list") // 새로 오픈 팝업 목록 조회
    public ResponseDto<List<PopupSummaryDto>> readNewList(HttpServletRequest request) {
        return ResponseDto.ok(listingPopupService.readNewList(request));
    }

    @GetMapping("/closing-list") // 종료 임박 팝업 목록 조회
    public ResponseDto<List<PopupSummaryDto>> readclosingList(HttpServletRequest request) {
        return ResponseDto.ok(listingPopupService.readClosingList(request));
    }

    @GetMapping("/interested-list") // 관심 팝업 목록 조회
    public ResponseDto<List<PopupStoreDto>> readInterestedList(@UserId Long userId) {
        log.info("Controller userId: {}", userId);
        return ResponseDto.ok(listingPopupService.readInterestedPopups(userId));
    }

    @GetMapping("/search") // 필터링 팝업 검색
    public ResponseDto<PagingResponseDto<List<PopupStoreDto>>> readSearchList(
            @RequestParam("searchName") String searchName,
            @RequestParam("filteringThreeCategories") String filteringThreeCategories,
            @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
            @RequestParam("operationStatus") EOperationStatus oper,
            @RequestParam("order") EPopupSort order,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            HttpServletRequest request) {
        return ResponseDto.ok(
                searchPopupService.readSearchingList(searchName, filteringThreeCategories, filteringFourteenCategories,
                        oper, order, page, size, request));
    }

    @GetMapping("/taste-list") // 취향 저격 팝업 목록
    public ResponseDto<PopupTasteDto> readTasteList(@UserId Long userId) {
        return ResponseDto.ok(listingPopupService.readTasteList(userId));
    }


    @GetMapping("/detail/{popupId}")
    public ResponseDto<PopupStoreDto> readPopup(@PathVariable String popupId, HttpServletRequest request) {
        return ResponseDto.ok(popupService.readPopupStore(popupId, request));
    }

    @GetMapping("/visited") // 마이 페이지 > 후기 작성하기 > 팝업 리스트
    public ResponseDto<List<VisitedPopupDto>> getVisitedPopupList(@UserId Long userId) {
        return ResponseDto.ok(popupService.getVisitedPopupList(userId));
    }
}
