package com.poppin.poppinserver.util;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationUtil {

    private final FirebaseMessaging firebaseMessaging;

    //안드로이드 테스트
    public void sendNotificationByTokenTest(String token, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            firebaseMessaging.send(message);
            log.info("알림을 성공적으로 전송했습니다.");
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            log.info("알림 보내기를 실패하였습니다");
        }
    }
}
