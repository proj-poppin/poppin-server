package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    // 성공시, 원하는 데이터를 공통 dto 담아 전달 인자로 보통에 경우에 dto 객체를 데이터로 전달
    @GetMapping("/hello")
    public ResponseDto<?> HelloController(){
        return ResponseDto.ok("Hello, Wolrd!");
    }

    // CommonException으로 에러를 터트리면 GlobalExceptionHandler에서 에러를 잡아
    // ResponseDto.fail()에 데이터를 담아 반환
    @GetMapping("/error")
    public ResponseDto<?> ErrorController(){
        throw new CommonException(ErrorCode.INVALID_PARAMETER);
    }
}
