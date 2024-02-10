package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.common.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/popup")
public class PopupController {
//    @GetMapping("/hot-list") // 인기 팝업 목록 조회
//    public ResponseDto<?> readHotList(){
//        return ResponseDto.ok();
//    }
}
