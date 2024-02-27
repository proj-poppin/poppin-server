package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
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
    public ResponseDto<?> createPopup(@RequestPart(value = "images", required=false) List<MultipartFile> images,
                                      @RequestPart(value = "contents") @Valid CreatePopupDto createPopupDto){
        log.info("controller.create_popup");
        return ResponseDto.ok(popupService.createPopup(createPopupDto, images));
    }

    @GetMapping("/hot-list") // 인기 팝업 목록 조회
    public ResponseDto<?> readHotList(){
        return ResponseDto.ok(popupService.readHotList());
    }

    @GetMapping("/new-list") // 새로 오픈 팝업 목록 조회
    public ResponseDto<?> readNewList(){
        return ResponseDto.ok(popupService.readNewList());
    }

    @GetMapping("/closing-list") // 새로 오픈 팝업 목록 조회
    public ResponseDto<?> readclosingList(){
        return ResponseDto.ok(popupService.readClosingList());
    }

    @GetMapping("/interested-list") // 관심 팝업 목록 조회
    public ResponseDto<?> readInterestedList() {
        Long userId = 1L;
        return ResponseDto.ok(popupService.readInterestedPopups(userId));
    }
}
