package com.poppin.poppinserver.util;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.NotificationTopic;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.repository.NotificationTopicRepository;
import com.poppin.poppinserver.type.ETopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SubscribeUtil {
    private final NotificationTopicRepository notificationTopicRepository;

    private final FirebaseMessaging firebaseMessaging;

    /* 안드로이드 토픽 구독 */
    public void androidSubscribeInterestedPopupTopic(NotificationToken token, Popup popup) throws FirebaseMessagingException {
        List<String> registrationTokens = Collections.singletonList(token.getToken());
        TopicManagementResponse response = null;

        // 관심 팝업 관련 주제에 대해서 구독
        for (ETopic topic : ETopic.values()){
            if (topic.equals(ETopic.OPEN) || topic.equals(ETopic.CHANGE_INFO) || topic.equals(ETopic.MAGAM)){ // 관심팝업 관련
                NotificationTopic notificationTopic = new NotificationTopic(popup, token, LocalDateTime.now(), topic);
                notificationTopicRepository.save(notificationTopic); // 저장

                String topicName = topic.getTopicName();
                response = firebaseMessaging.subscribeToTopic(registrationTokens, topicName); // 구독
            }
        }

        log.info(response.getSuccessCount() + " token(s) were subscribed successfully");
    }

    public void androidUnsubscribeInterestedPopupTopic(NotificationToken token) throws FirebaseMessagingException {
        List<String> registrationTokens = Collections.singletonList(token.getToken());
        TopicManagementResponse response = null;

        // 관심 팝업 관련 주제에 대해서 구독 해제
        for (ETopic topic : ETopic.values()){
            if (topic.equals(ETopic.OPEN) || topic.equals(ETopic.CHANGE_INFO) || topic.equals(ETopic.MAGAM)) { // 관심팝업 관련
                NotificationTopic notificationTopic = notificationTopicRepository.findByTokenAndTopic(token.getToken(), topic);
                notificationTopicRepository.delete(notificationTopic); // 삭제

                String topicName = topic.getTopicName();
                response = firebaseMessaging.unsubscribeFromTopic(registrationTokens, topicName); // 구독 해제
            }
        }

        log.info(response.getSuccessCount() + " token(s) were unsubscribed successfully");
    }

    public void androidSubscribeReopenPopupTopic(NotificationToken token, Popup popup) throws FirebaseMessagingException {
        List<String> registrationTokens = Collections.singletonList(token.getToken());
        TopicManagementResponse response = null;

        // 재오픈 수요체크 주제에 대해서 구독
        for (ETopic topic : ETopic.values()){
            if (topic.equals(ETopic.REOPEN)){ // 재오픈 수요체크
                NotificationTopic notificationTopic = new NotificationTopic(popup, token, LocalDateTime.now(), topic);
                notificationTopicRepository.save(notificationTopic); // 저장

                String topicName = topic.getTopicName();
                response = firebaseMessaging.subscribeToTopic(registrationTokens, topicName); // 구독
            }
        }

        log.info(response.getSuccessCount() + " token(s) were subscribed successfully");
    }
}
