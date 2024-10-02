package com.poppin.poppinserver.alarm.controller;

import com.poppin.poppinserver.alarm.service.FCMTokenService;
import com.poppin.poppinserver.core.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
public class FCMController {

    //TODO: 삭제 예정
      private final FCMTokenService fcmTokenService;
//    private final FCMTestUtil FCMTestUtil;
//
//    @PostMapping("/token/test")
//    public void sendNotificationByTokenTest(@Valid @RequestBody PushDto pushDto) {
//        FCMTestUtil.sendNotificationByTokenTest(pushDto);
//    }
//
//    @PostMapping("topic/test")
//    public void sendAndroidNotificationByTopicTest(@Valid @RequestBody PushDto pushDto)
//            throws FirebaseMessagingException {
//        FCMTestUtil.sendNotificationByTopicTest(pushDto);
//    }

    //TODO: 삭제 예정
//    /* 알림 허용 시 데이터 저장 */
//    @PostMapping("/apply/FCMtoken")
//    public ResponseDto<?> addFCMTokenUsers(@RequestBody ApplyTokenRequestDto applyTokenRequestDto) {
//        return ResponseDto.ok(fcmTokenService.fcmApplyToken(applyTokenRequestDto));
//    }

    // 데이터베이스 초기화 시 토큰 팝업 구독 해제
    @PostMapping("/reset/topic")
    public ResponseDto<?> resetPopupTopic() {
        return ResponseDto.ok(fcmTokenService.resetPopupTopic());
    }
}
