package com.poppin.poppinserver.controller;

import com.poppin.poppinserver.constant.Constant;
import com.poppin.poppinserver.dto.auth.request.SocialRegisterRequestDto;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.auth.request.AuthSignUpDto;
import com.poppin.poppinserver.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    // 자체 회원가입 API
    @PostMapping("/sign-up")
    public ResponseDto<?> authSignUp(@RequestBody @Valid AuthSignUpDto authSignUpDto) {
        authService.authSignUp(authSignUpDto);
        log.info("authSignUpDto : " + authSignUpDto);
        return ResponseDto.created(null);
    }

//    @PostMapping("/register")
//    public ResponseDto<?> socialRegister(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken,
//                                         @RequestBody @Valid SocialRegisterRequestDto socialRegisterRequestDto) {
//        log.info("socialRegisterRequestDto : " + socialRegisterRequestDto);
//        return ResponseDto.created(authService.socialRegister(accessToken, socialRegisterRequestDto));
//    }

    @PostMapping("/login/kakao")
    public ResponseDto<?> authKakaoLogin(@NotNull @RequestHeader(Constant.AUTHORIZATION_HEADER) String accessToken) {
        log.info("accessToken : " + accessToken);   // 'bearer ' 제거 필요
        return ResponseDto.ok(authService.authKakaoLogin(accessToken));
    }

}
