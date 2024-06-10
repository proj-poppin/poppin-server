package com.poppin.poppinserver.util.push.android;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.poppin.poppinserver.domain.InformationTopic;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.PopupTopic;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.repository.InformationTopicRepository;
import com.poppin.poppinserver.repository.PopupTopicRepository;
import com.poppin.poppinserver.type.EInformationTopic;
import com.poppin.poppinserver.type.EPopupTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMSubscribeUtil {

    private final PopupTopicRepository popupTopicRepository;
    private final InformationTopicRepository informationTopicRepository;

    private final FirebaseMessaging firebaseMessaging;

    /* 안드로이드 토픽 구독 */
    public void subscribePopupTopic(NotificationToken token, Popup popup, String type) throws FirebaseMessagingException {
        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(token.getToken());

        TopicManagementResponse response = null;

        // 관심 팝업 관련 주제에 대해서 구독
        for (EPopupTopic topic : EPopupTopic.values()){
            if (topic.getTopicType().equals(type)){ // 관심팝업 관련
                PopupTopic popupTopic = new PopupTopic(token, popup, type, LocalDateTime.now(), topic);
                popupTopicRepository.save(popupTopic); // 저장

                String topicName = topic.getTopicName();
                response = firebaseMessaging.subscribeToTopic(registrationTokens, topicName); // 구독
            }
        }

        log.info(response.getSuccessCount() + " token(s) were subscribed successfully : " + token.getToken());
    }

    public void unsubscribePopupTopic(NotificationToken token,String type) throws FirebaseMessagingException {

        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(token.getToken());

        TopicManagementResponse response = null;

        // 구독 해제
        for (EPopupTopic topic : EPopupTopic.values()){
            if (topic.getTopicType().equals(type)) {
                PopupTopic popupTopic = popupTopicRepository.findByTokenAndTopic(token.getToken(), topic);
                popupTopicRepository.delete(popupTopic);

                String topicName = topic.getTopicName();
                response = firebaseMessaging.unsubscribeFromTopic(registrationTokens, topicName); // 구독 해제
            }
        }

        log.info(response.getSuccessCount() + " token(s) were unsubscribed successfully");
    }


    public void androidSubscribeNotificationTopic(NotificationToken token, Popup popup) throws FirebaseMessagingException {

        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(token.getToken());

        TopicManagementResponse response = null;

        if (popup != null){
            // 재오픈 수요체크 주제에 대해서 구독
            for (EInformationTopic topic : EInformationTopic.values()){
                if (topic.equals(EInformationTopic.NOTI)){ // 재오픈 수요체크
                    InformationTopic informationTopic = new InformationTopic(token, topic.getTopicType(), LocalDateTime.now(), topic);
                    informationTopicRepository.save(informationTopic); // 저장

                    String topicName = topic.getTopicName();
                    response = firebaseMessaging.subscribeToTopic(registrationTokens, topicName); // 구독
                }
            }
            log.info(response.getSuccessCount() + " token(s) were subscribed successfully");
        }
    }
}
