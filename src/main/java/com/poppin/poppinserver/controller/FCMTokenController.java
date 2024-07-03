package com.poppin.poppinserver.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.fcm.request.PushDto;
import com.poppin.poppinserver.dto.fcm.request.TokenRequestDto;
import com.poppin.poppinserver.service.FCMService;
import com.poppin.poppinserver.util.push.android.FCMTestUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
public class FCMTokenController {

    private final FCMService fcmService;
    private final FCMTestUtil FCMTestUtil;

    @PostMapping("/token/test")
    public void sendNotificationByTokenTest(@Valid @RequestBody PushDto pushDto){
        FCMTestUtil.sendNotificationByTokenTest(pushDto);
    }

    @PostMapping("topic/test")
    public void sendAndroidNotificationByTopicTest(@Valid @RequestBody PushDto pushDto) throws FirebaseMessagingException {
        FCMTestUtil.sendNotificationByTopicTest(pushDto);
    }

    /*알림 허용 시 데이터 저장*/
    @PostMapping("/apply/FCMtoken")
    public ResponseDto<?> addFCMTokenUsers(@RequestBody TokenRequestDto tokenRequestDto){
        return ResponseDto.ok(fcmService.fcmApplyToken(tokenRequestDto));
    }
}
