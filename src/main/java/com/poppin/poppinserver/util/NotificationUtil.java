package com.poppin.poppinserver.util;

import com.google.firebase.messaging.*;

import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.NotificationTopic;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
import com.poppin.poppinserver.dto.notification.request.PushDto;
import com.poppin.poppinserver.repository.NotificationTokenRepository;
import com.poppin.poppinserver.repository.NotificationTopicRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class NotificationUtil {

    private static final String ANDROID_TOKEN_TEST = "[FCM Android Token Test]";
    private static final String ANDROID_TOPIC_TEST = "[FCM Android Topic Test]";
    private static final String ANDROID_TOKEN = "[FCM Android Token]";
    private static final String ANDROID_TOPIC = "[FCM Android Topic]";

    private final UserRepository userRepository;
    private final PopupRepository popupRepository;
    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationTopicRepository notificationTopicRepository;


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

    /* 안드로이드 토픽 구독 & 발송 테스트 */
    public void sendAndroidNotificationByTopicTest(PushDto pushDto) throws FirebaseMessagingException {
        List<String> registrationTokens = Collections.singletonList(pushDto.token());

        TopicManagementResponse response = firebaseMessaging.subscribeToTopic(registrationTokens, "Test");

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

    /* 안드로이드 토픽 구독 */
    public void androidSubscribeTopic(List<NotificationToken> tokenList, Popup popup) throws FirebaseMessagingException {
        List<String> registrationTokens = null;

        // 데이터베이스 저장
        for (NotificationToken token : tokenList){
            NotificationTopic notificationTopic = new NotificationTopic(popup, token, LocalDateTime.now());
            notificationTopicRepository.save(notificationTopic);
            registrationTokens = Collections.singletonList(token.getToken());
        }

        String topic = popup.getName();
        TopicManagementResponse response = firebaseMessaging.subscribeToTopic(registrationTokens, topic);

        log.info(response.getSuccessCount() + " tokens were subscribed successfully");
    }

    /* 안드로이드 토픽 메시지 발송 */
    public void sendFCMTopic(List<FCMRequestDto> fcmRequestDtoList)throws FirebaseMessagingException {
        List<String> tokenList = null;
        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList){
            tokenList = IntStream.rangeClosed(1, 30)
                    .mapToObj(index -> fcmRequestDto.token())
                    .collect(Collectors.toList());
        }
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(fcmRequestDtoList.get(0).title())
                        .setBody(fcmRequestDtoList.get(0).body())
                        .setImage(fcmRequestDtoList.get(0).image())
                        .build()
                )
                .addAllTokens(tokenList)
                .build();
        if (message != null){
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                log.error(ANDROID_TOPIC + " List of tokens that caused failures: " + failedTokens);
            }else log.info(ANDROID_TOPIC + " List of tokens send messages SUCCESSFULLY");
        }
    }
}
