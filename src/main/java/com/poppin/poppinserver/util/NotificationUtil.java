package com.poppin.poppinserver.util;

import com.google.firebase.messaging.*;

import com.poppin.poppinserver.dto.notification.request.PushDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationUtil {

    private static final String ANDROID_TOKEN_TEST = "[FCM Android Token Test]";
    private static final String ANDROID_TOPIC_TEST = "[FCM Android Topic Test]";

    private final FirebaseMessaging firebaseMessaging;

    /* 안드로이드 토큰 테스트 */
    public void sendAndroidNotificationByTokenTest(PushDto pushDto) {

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(pushDto.title())
                        .setBody(pushDto.body())
                        .build())
                .setToken(pushDto.token())
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.debug(ANDROID_TOKEN_TEST + " Successfully sent message: " + result);
        } catch (FirebaseMessagingException e) {
            log.error(ANDROID_TOKEN_TEST + " Failed to send message: " + e.getMessage());
        }
    }

    /* 안드로이드 토픽 테스트 */
    public void sendAndroidNotificationByTopicTest(PushDto pushDto) throws FirebaseMessagingException {
        List<String> registrationTokens = Collections.singletonList(pushDto.token());

        TopicManagementResponse response = firebaseMessaging.subscribeToTopic(Collections.singletonList(pushDto.token()), "Test");

        log.info(response.getSuccessCount() + " tokens were subscribed successfully");

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(pushDto.title())
                        .setBody(pushDto.body())
                        .build())
                .setTopic("Test")
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.debug(ANDROID_TOPIC_TEST + " Successfully sent message: " + result);
        } catch (FirebaseMessagingException e) {
            log.error(ANDROID_TOPIC_TEST + " Failed to send message: " + e.getMessage());
        }
    }
}
