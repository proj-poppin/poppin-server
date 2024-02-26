package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.PopupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup")
public class PopupController {
    private final PopupService popupService;

    @PostMapping("/create-popup") // 팝업생성 !!! 관리자 계정인지 확인하는 로직 필요
    public ResponseDto<?> createPopup(@RequestBody @Valid CreatePopupDto createPopupDto, @RequestParam("files") MultipartFile files){
        return ResponseDto.ok(popupService.createPopup(createPopupDto,files));
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
