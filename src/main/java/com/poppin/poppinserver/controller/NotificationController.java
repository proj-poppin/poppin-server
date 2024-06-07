package com.poppin.poppinserver.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.notification.request.PushDto;
import com.poppin.poppinserver.dto.notification.request.TokenRequestDto;
import com.poppin.poppinserver.service.NotificationService;
import com.poppin.poppinserver.util.FCMSendUtil;
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
public class NotificationController {

    private final NotificationService notificationService;
    private final FCMSendUtil FCMSendUtil;

    @PostMapping("/android/token/test")
    public void sendAndroidNotificationByTokenTest(@Valid @RequestBody PushDto pushDto){
        FCMSendUtil.sendAndroidNotificationByTokenTest(pushDto);
    }

    @PostMapping("/android/topic/test")
    public void sendAndroidNotificationByTopicTest(@Valid @RequestBody PushDto pushDto) throws FirebaseMessagingException {
        FCMSendUtil.sendAndroidNotificationByTopicTest(pushDto);
    }

    /*알림 허용 시 데이터 저장*/
    @PostMapping("/apply/FCMtoken")
    public ResponseDto<?> addFCMTokenUsers(@RequestBody TokenRequestDto tokenRequestDto){
        return ResponseDto.ok(notificationService.fcmApplyToken(tokenRequestDto));
    }
}
