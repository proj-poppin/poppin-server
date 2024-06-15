package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.managerInform.request.UpdateManagerInfromDto;
import com.poppin.poppinserver.dto.notification.request.PushRequestDto;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.popup.request.UpdatePopupDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.service.PopupService;
import com.poppin.poppinserver.service.S3Service;
import com.poppin.poppinserver.type.EOperationStatus;
import com.poppin.poppinserver.type.EPopupSort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup")
public class PopupController {
    private final PopupService popupService;


    @PostMapping(value = "/admin", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> createPopup(@RequestPart(value = "images") List<MultipartFile> images,
                                      @RequestPart(value = "contents") @Valid CreatePopupDto createPopupDto,
                                      @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(popupService.createPopup(createPopupDto, images, adminId));
    } // 전체팝업관리 - 팝업생성

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseDto<?> readPopup(@RequestParam("id") Long popupId,
                                    @UserId Long adminId) {

        return ResponseDto.ok(popupService.readPopup(adminId, popupId));
    } // 전체팝업관리 - 팝업조회

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/list")
    public ResponseDto<?> readManageList(@RequestParam("oper") EOperationStatus oper,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size,
                                         @UserId Long adminId) {
        return ResponseDto.ok(popupService.readManageList(adminId, oper, page, size));
    } // 전체팝업관리 - 전체 팝업 리스트 조회

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin")
    public ResponseDto<?> removePopup(@RequestParam("id") Long popupId){
        return ResponseDto.ok(popupService.removePopup(popupId));
    } // 전체팝업관리 - 팝업 삭제

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/admin", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> uploadManagerInform(@RequestPart(value = "images") List<MultipartFile> images,
                                              @RequestPart(value = "contents") @Valid UpdatePopupDto updatePopupDto,
                                              @UserId Long adminId) {

        if (images.isEmpty()) {
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(popupService.updatePopup(updatePopupDto, images, adminId));
    } // 전체팝업관리 - 팝업 수정

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search")
    public ResponseDto<?> readManageList(@RequestParam("text") String text,
                                         @RequestParam("oper") EOperationStatus oper,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size) {
        return ResponseDto.ok(popupService.searchManageList(text, page, size, oper));
    } // 전체팝업관리 - 전체 팝업 검색

    @GetMapping("/guest/detail")
    public ResponseDto<?> readGuestDetail(@RequestParam("popupId") Long popupId) {

        return ResponseDto.ok(popupService.readGuestDetail(popupId));
    } // 비로그인 상세조회

    @GetMapping("/detail")
    public ResponseDto<?> readDetail(@RequestParam("popupId") Long popupId, @UserId Long userId) {

        return ResponseDto.ok(popupService.readDetail(popupId, userId));
    } // 로그인 상세조회

    @GetMapping("/hot-list") // 인기 팝업 목록 조회
    public ResponseDto<?> readHotList() {
        return ResponseDto.ok(popupService.readHotList());
    }

    @GetMapping("/new-list") // 새로 오픈 팝업 목록 조회
    public ResponseDto<?> readNewList() {
        return ResponseDto.ok(popupService.readNewList());
    }

    @GetMapping("/closing-list") // 종료 임박 팝업 목록 조회
    public ResponseDto<?> readclosingList() {
        return ResponseDto.ok(popupService.readClosingList());
    }

    @GetMapping("/interested-list") // 관심 팝업 목록 조회
    public ResponseDto<?> readInterestedList(@UserId Long userId) {
        log.info("Controller userId: {}", userId);
        return ResponseDto.ok(popupService.readInterestedPopups(userId));
    }

    @GetMapping("/search") // 로그인 팝업 검색
    public ResponseDto<?> readSearchList(@RequestParam("text") String text,
                                         @RequestParam("taste") String taste,
                                         @RequestParam("prepered") String prepered,
                                         @RequestParam("oper") EOperationStatus oper,
                                         @RequestParam("order") EPopupSort order,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size,
                                         @UserId Long userId) {
        return ResponseDto.ok(popupService.readSearchingList(text, taste, prepered, oper, order, page, size, userId));
    }

    @GetMapping("/search/base") // 로그인 팝업 베이스 검색
    public ResponseDto<?> readBaseList(@RequestParam("text") String text,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size,
                                         @UserId Long userId) {
        return ResponseDto.ok(popupService.readBaseList(text, page, size, userId));
    }

    @GetMapping("/guest/search") // 비로그인 팝업 검색
    public ResponseDto<?> readGuestSearchList(@RequestParam("text") String text,
                                              @RequestParam("taste") String taste,
                                              @RequestParam("prepered") String prepered,
                                              @RequestParam("oper") EOperationStatus oper,
                                              @RequestParam("order") EPopupSort order,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size) {
        return ResponseDto.ok(popupService.readGuestSearchingList(text, taste, prepered, oper, order, page, size));
    }

    @GetMapping("/guest/search/base") // 비로그인 팝업 베이스 검색
    public ResponseDto<?> readGuestBaseList(@RequestParam("text") String text,
                                       @RequestParam("page") int page,
                                       @RequestParam("size") int size) {
        return ResponseDto.ok(popupService.readGuestBaseList(text, page, size));
    }

    @GetMapping("/taste-list") // 취향 저격 팝업 목록
    public ResponseDto<?> readTasteList(@UserId Long userId) {
        return ResponseDto.ok(popupService.readTasteList(userId));
    }

    @PostMapping("/reopen") // 재오픈 수요
    public ResponseDto<?> reopenDemand(@UserId Long userId, @RequestBody PushRequestDto pushRequestDto){
        return ResponseDto.ok(popupService.reopenDemand(userId, pushRequestDto));
    }
}
