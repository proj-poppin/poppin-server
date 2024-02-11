package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.Popup.request.CreatePopupDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.service.PopupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup")
public class PopupController {
    private final PopupService popupService;

    @PostMapping("/create-popup") // 팝업생성 !!! 관리자 계정인지 확인하는 로직 필요
    public ResponseDto<?> createPopup(@RequestBody @Valid CreatePopupDto createPopupDto){
        return ResponseDto.ok(popupService.createPopup(createPopupDto));
    }

    @GetMapping("/hot-list") // 인기 팝업 목록 조회
    public ResponseDto<?> readHotList(){
        return ResponseDto.ok(popupService.readHotList());
    }
}
