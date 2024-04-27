package com.poppin.poppinserver.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.dto.common.ResponseDto;
import com.poppin.poppinserver.dto.notification.request.PushDto;
import com.poppin.poppinserver.dto.notification.request.TokenRequestDto;
import com.poppin.poppinserver.service.NotificationService;
import com.poppin.poppinserver.util.NotificationUtil;
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
    private final NotificationUtil notificationUtil;

    @PostMapping("/android/token/test")
    public void sendAndroidNotificationByTokenTest(@Valid @RequestBody PushDto pushDto){
        notificationUtil.sendAndroidNotificationByTokenTest(pushDto);
    }

    @PostMapping("/android/topic/test")
    public void sendAndroidNotificationByTopicTest(@Valid @RequestBody PushDto pushDto) throws FirebaseMessagingException {
        notificationUtil.sendAndroidNotificationByTopicTest(pushDto);
    }

    /*알림 허용 시 데이터 저장*/
    @PostMapping("/add/token")
    public ResponseDto<?> addTokenUsers(@RequestBody TokenRequestDto tokenRequestDto){
        return ResponseDto.ok(notificationService.addToken(tokenRequestDto));
    }
}
