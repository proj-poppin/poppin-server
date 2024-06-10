package com.poppin.poppinserver.util.push.android;

import com.google.firebase.messaging.*;

import com.poppin.poppinserver.config.APNsConfig;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;

import com.poppin.poppinserver.repository.NotificationTokenRepository;
import com.poppin.poppinserver.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMSendUtil {

    private final NotificationTokenRepository notificationTokenRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final APNsConfig apnsConfiguration;

    private final AlarmService alarmService;



    /**
     * 안드로이드 FCM Topic 앱 푸시 알림 메서드
     * @param fcmRequestDtoList
     * @throws FirebaseMessagingException
     */
    public void sendFCMTopicMessage(List<FCMRequestDto> fcmRequestDtoList){
        for (FCMRequestDto fcmRequestDto : fcmRequestDtoList){

            Message message = null;
            NotificationToken notificationToken = notificationTokenRepository.findByToken(fcmRequestDto.token());
            if (notificationToken.getDevice().equals("android")) {
                // aos
                message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(fcmRequestDto.title())
                                .setBody(fcmRequestDto.body())
                                .build())
                        .setToken(fcmRequestDto.token())
                        .setTopic(String.valueOf(fcmRequestDto.topic()))
                        .build();
            }
            else{
                // ios
                ApnsConfig apnsConfig = apnsConfiguration.createApnsConfig(fcmRequestDto.title(), fcmRequestDto.body());
                message = Message.builder()
                        .setApnsConfig(apnsConfig)
                        .setToken(fcmRequestDto.token())
                        .setTopic(String.valueOf(fcmRequestDto.topic()))
                        .build();
            }
            log.info("토픽 메시지 발송 시작");
            try {

                String result = firebaseMessaging.send(message);
                log.debug( " Successfully sent message: " + result);

                // 알림 키워드 등록
                String flag = alarmService.insertPopupAlarmKeyword(fcmRequestDto);
                if (flag.equals("1")){
                    log.info(fcmRequestDto.token()+"에 대한 알림 키워드 등록 완료");
                }else{
                    log.error(fcmRequestDto.token() + "에 대한 알림 키워드 등록 실패");
                }
            } catch (FirebaseMessagingException e) {
                log.error(" Failed to send message: " + e.getMessage());
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
            tokenList = IntStream.rangeClosed(1, 1000)
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
                log.error(" List of tokens that caused failures: " + failedTokens);
            }else log.info(" List of tokens send messages SUCCESSFULLY");
        }
    }

}
