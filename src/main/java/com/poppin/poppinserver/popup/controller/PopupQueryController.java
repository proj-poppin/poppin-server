package com.poppin.poppinserver.popup.controller;

import com.poppin.poppinserver.core.annotation.UserId;
import com.poppin.poppinserver.core.dto.ResponseDto;
import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.core.type.EPopupSort;
import com.poppin.poppinserver.popup.service.AdminPopupService;
import com.poppin.poppinserver.popup.service.ListingPopupService;
import com.poppin.poppinserver.popup.service.PopupService;
import com.poppin.poppinserver.popup.service.SearchPopupService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup")
public class PopupQueryController {
    private final PopupService popupService;
    private final ListingPopupService listingPopupService;
    private final SearchPopupService searchPopupService;

    private final AdminPopupService adminPopupService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseDto<?> readPopup(@RequestParam("id") Long popupId) {
        log.info(LocalDateTime.now().toString());
        return ResponseDto.ok(adminPopupService.readPopup(popupId));
    } // 전체팝업관리 - 팝업조회

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/list")
    public ResponseDto<?> readManageList(@RequestParam("oper") EOperationStatus oper,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size,
                                         @UserId Long adminId) {
        return ResponseDto.ok(adminPopupService.readManageList(adminId, oper, page, size));
    } // 전체팝업관리 - 전체 팝업 리스트 조회


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search")
    public ResponseDto<?> readManageList(@RequestParam("text") String text,
                                         @RequestParam("oper") EOperationStatus oper,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size) {
        return ResponseDto.ok(adminPopupService.searchManageList(text, page, size, oper));
    } // 전체팝업관리 - 전체 팝업 검색

    @GetMapping("/guest/detail")
    public ResponseDto<?> readGuestDetail(@RequestParam("popupId") String popupId) {

        return ResponseDto.ok(popupService.readGuestDetail(popupId));
    } // 비로그인 상세조회

    @GetMapping("/detail")
    public ResponseDto<?> readDetail(@RequestParam("popupId") String popupId, @UserId Long userId) {

        return ResponseDto.ok(popupService.readDetail(popupId, userId));
    } // 로그인 상세조회

    @GetMapping("/hot-list") // 인기 팝업 목록 조회
    public ResponseDto<?> readHotList() {
        return ResponseDto.ok(listingPopupService.readHotList());
    }

    @GetMapping("/new-list") // 새로 오픈 팝업 목록 조회
    public ResponseDto<?> readNewList() {
        return ResponseDto.ok(listingPopupService.readNewList());
    }

    @GetMapping("/closing-list") // 종료 임박 팝업 목록 조회
    public ResponseDto<?> readclosingList() {
        return ResponseDto.ok(listingPopupService.readClosingList());
    }

    @GetMapping("/interested-list") // 관심 팝업 목록 조회
    public ResponseDto<?> readInterestedList(@UserId Long userId) {
        log.info("Controller userId: {}", userId);
        return ResponseDto.ok(listingPopupService.readInterestedPopups(userId));
    }

    @GetMapping("/search") // 로그인 팝업 검색
    public ResponseDto<?> readSearchList(@RequestParam("searchName") String searchName,
                                         @RequestParam("filteringThreeCategories") String filteringThreeCategories,
                                         @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
                                         @RequestParam("operationStatus") EOperationStatus oper,
                                         @RequestParam("order") EPopupSort order,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size,
                                         @UserId Long userId) {
        return ResponseDto.ok(
                searchPopupService.readSearchingList(searchName, filteringThreeCategories, filteringFourteenCategories,
                        oper, order, page, size, userId));
    }

    @GetMapping("/search/base") // 로그인 팝업 베이스 검색
    public ResponseDto<?> readBaseList(@RequestParam("searchName") String searchName,
                                       @RequestParam("page") int page,
                                       @RequestParam("size") int size,
                                       @UserId Long userId) {
        return ResponseDto.ok(searchPopupService.readBaseList(searchName, page, size, userId));
    }

    @GetMapping("/guest/search") // 비로그인 팝업 검색
    public ResponseDto<?> readGuestSearchList(@RequestParam("searchName") String searchName,
                                              @RequestParam("filteringThreeCategories") String filteringThreeCategories,
                                              @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
                                              @RequestParam("operationStatus") EOperationStatus oper,
                                              @RequestParam("order") EPopupSort order,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size) {
        return ResponseDto.ok(searchPopupService.readGuestSearchingList(searchName, filteringThreeCategories,
                filteringFourteenCategories, oper, order, page, size));
    }

    @GetMapping("/guest/search/base") // 비로그인 팝업 베이스 검색
    public ResponseDto<?> readGuestBaseList(@RequestParam("searchName") String searchName,
                                            @RequestParam("page") int page,
                                            @RequestParam("size") int size) {
        return ResponseDto.ok(searchPopupService.readGuestBaseList(searchName, page, size));
    }

    @GetMapping("/taste-list") // 취향 저격 팝업 목록
    public ResponseDto<?> readTasteList(@UserId Long userId) {
        return ResponseDto.ok(listingPopupService.readTasteList(userId));
    }


    @GetMapping("/detail/{popupId}")
    public ResponseDto<?> readPopup(@PathVariable String popupId, HttpServletRequest request) {
        return ResponseDto.ok(popupService.readPopupStore(popupId, request));
    }
}
