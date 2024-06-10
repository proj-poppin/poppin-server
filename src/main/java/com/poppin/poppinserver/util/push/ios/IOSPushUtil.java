package com.poppin.poppinserver.util.push.ios;

import com.google.firebase.messaging.*;
import com.poppin.poppinserver.config.APNsConfig;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
import com.poppin.poppinserver.dto.notification.request.PushDto;
import com.poppin.poppinserver.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IOSPushUtil {
    /**=================================================================================== */
    private static final String IOS_TOKEN_TEST = "[FCM IOS Token Test]";
    private static final String IOS_TOPIC_TEST = "[FCM IOS Topic Test]";
    private static final String IOS_TOKEN = "[FCM IOS Token]";
    private static final String IOS_TOPIC = "[FCM IOS Topic]";
    /**==================================================================================== */

    private final FirebaseMessaging firebaseMessaging;
    private final APNsConfig apnsConfiguration;

    private final AlarmService alarmService;


    /* 아이폰 토큰 테스트 */
    public void sendIOSNotificationByTokenTest(PushDto pushDto) {

        ApnsConfig apNsConfig = apnsConfiguration.createApnsConfig(pushDto.title(), pushDto.body());
        Message message = Message.builder()
                .setToken(pushDto.token())
                .setApnsConfig(apNsConfig)
                .build();

        try {
            String result = firebaseMessaging.send(message);
            log.debug(IOS_TOKEN_TEST + " Successfully sent message: " + result);
        } catch (FirebaseMessagingException e) {
            log.error(IOS_TOKEN_TEST + " Failed to send message: " + e.getMessage());
        }
    }

    /* 안드로이드 토픽 구독 & 발송 테스트 */
    public void sendIOSNotificationByTopicTest(PushDto pushDto) throws FirebaseMessagingException {
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
            log.debug(IOS_TOPIC_TEST + " Successfully sent message: " + result);
        } catch (FirebaseMessagingException e) {
            log.error(IOS_TOPIC_TEST + " Failed to send message: " + e.getMessage());
        }
    }



    public void sendFCMTopicMessage(List<FCMRequestDto> fcmRequestDtoList){
        List<String> registrationTokens = null;
        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList){
            registrationTokens.add(fcmRequestDto.token());
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(fcmRequestDto.title())
                            .setBody(fcmRequestDto.body())
                            .build())
                    .setTopic(String.valueOf(fcmRequestDto.topic()))
                    .build();

            log.info("--- 토픽 메시지 발송 시작 ---");
            try {
                String result = firebaseMessaging.send(message);
                log.debug(IOS_TOPIC + " Successfully sent message: " + result);

                // 알림 키워드 등록
                String flag = alarmService.insertAlarmKeyword(fcmRequestDto);
                if (flag.equals("1")){
                    log.info(fcmRequestDto.token()+"에 대한 알림 키워드 등록 완료");
                }else{
                    log.error(fcmRequestDto.token() + "에 대한 알림 키워드 등록 실패");
                }
            } catch (FirebaseMessagingException e) {
                log.error(IOS_TOPIC + " Failed to send message: " + e.getMessage());
            }
        }

    }

    /**
     * 여러 기기 동시 전송
     * @param fcmRequestDtoList
     * @throws FirebaseMessagingException
     */
    public void sendMultiDeviceMessage(List<FCMRequestDto> fcmRequestDtoList)throws FirebaseMessagingException {
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
                log.error(IOS_TOPIC + " List of tokens that caused failures: " + failedTokens);
            }else log.info(IOS_TOPIC + " List of tokens send messages SUCCESSFULLY");
        }
    }

}
