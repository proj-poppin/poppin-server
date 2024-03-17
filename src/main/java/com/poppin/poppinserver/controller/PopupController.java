package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.annotation.UserId;
import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.service.PopupService;
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
@RequestMapping("/api/v1/popup")
public class PopupController {
    private final PopupService popupService;

    @PostMapping(value = "/create-popup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}) // 팝업생성 !!! 관리자 계정인지 확인하는 로직 필요
    public ResponseDto<?> createPopup(@RequestPart(value = "images") List<MultipartFile> images,
                                      @RequestPart(value = "contents") @Valid CreatePopupDto createPopupDto){

        if(images.isEmpty()){
            throw new CommonException(ErrorCode.MISSING_REQUEST_IMAGES);
        }

        return ResponseDto.ok(popupService.createPopup(createPopupDto, images));
    }

    @GetMapping("/detail")
    public ResponseDto<?> readDetail(@RequestParam("popup_id") Long popupId){
        return ResponseDto.ok(popupService.readDetail(popupId));
    }

    @GetMapping("/hot-list") // 인기 팝업 목록 조회
    public ResponseDto<?> readHotList(){
        return ResponseDto.ok(popupService.readHotList());
    }

    @GetMapping("/new-list") // 새로 오픈 팝업 목록 조회
    public ResponseDto<?> readNewList(){
        return ResponseDto.ok(popupService.readNewList());
    }

    @GetMapping("/closing-list") // 종료 임박 팝업 목록 조회
    public ResponseDto<?> readclosingList(){
        return ResponseDto.ok(popupService.readClosingList());
    }

    @GetMapping("/interested-list") // 관심 팝업 목록 조회
    public ResponseDto<?> readInterestedList(@UserId Long userId) {
        log.info("Controller userId: {}", userId);
        return ResponseDto.ok(popupService.readInterestedPopups(userId));
    }

    @GetMapping("/search") // 팝업 검색
    public ResponseDto<?> readSearchList(@RequestParam("text") String text,
                                         @RequestParam("page") int page,
                                         @RequestParam("size") int size,
                                         @UserId Long userId){
        log.info(text);
        log.info(String.valueOf(page));
        log.info(String.valueOf(size));
        log.info(userId.toString());
        return ResponseDto.ok(popupService.readSearchingList(text, page, size, userId));
    }
}
