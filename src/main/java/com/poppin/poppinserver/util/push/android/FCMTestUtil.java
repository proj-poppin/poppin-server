package com.poppin.poppinserver.util.push.android;

import com.google.firebase.messaging.*;
import com.poppin.poppinserver.config.APNsConfiguration;
import com.poppin.poppinserver.config.AndroidConfiguration;
import com.poppin.poppinserver.dto.notification.request.PushDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMTestUtil {

    private static final String TOKEN_TEST = "[FCM Token Test]";
    private static final String TOPIC_TEST = "[FCM Topic Test]";

    private final FirebaseMessaging firebaseMessaging;
    private final APNsConfiguration apnsConfiguration;
    private final AndroidConfiguration androidConfiguration;

    /* 안드로이드 토큰 테스트 */
    public void sendNotificationByTokenTest(PushDto pushDto) {

        log.info("token : " + pushDto.token());
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(pushDto.title())
                        .setBody(pushDto.body())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig())
                .setAndroidConfig(androidConfiguration.androidConfig())
                .setToken(pushDto.token())
                .putData("popupId", pushDto.popupId().toString() )
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.info(TOKEN_TEST + " Successfully sent message: " + result);
        } catch (FirebaseMessagingException e) {
            log.error(TOKEN_TEST + " Failed to send message: " + e.getMessage());
        }
    }

    /* 안드로이드 토픽 구독 & 발송 테스트 */
    public void sendNotificationByTopicTest(PushDto pushDto) throws FirebaseMessagingException {
        List<String> registrationTokens = Collections.singletonList(pushDto.token());

        TopicManagementResponse response = firebaseMessaging.subscribeToTopic(registrationTokens, "Test");

        log.info(response.getSuccessCount() + " tokens were subscribed successfully");

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(pushDto.title())
                        .setBody(pushDto.body())
                        .build())
                .setApnsConfig(apnsConfiguration.apnsConfig())
                //.setAndroidConfig(androidConfiguration.androidConfig())
                .setToken(pushDto.token())
                .setTopic("Test")
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.debug(TOPIC_TEST + " Successfully sent message: " + result);
        } catch (FirebaseMessagingException e) {
            log.error(TOPIC_TEST + " Failed to send message: " + e.getMessage());
        }
    }


}
