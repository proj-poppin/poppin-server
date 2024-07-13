package com.poppin.poppinserver.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.poppin.poppinserver.domain.FCMToken;
import com.poppin.poppinserver.domain.PopupTopic;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.repository.PopupTopicRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMSubscribeService {

    private final PopupTopicRepository popupTopicRepository;

    private final FirebaseMessaging firebaseMessaging;

    /* 안드로이드 토픽 구독 */
    public void subscribePopupTopic(FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException {
        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(token.getToken());

        TopicManagementResponse response = null;

        log.info("subscribe start");

        // 중복 저장되는 오류 방어 코드 작성
        PopupTopic topicExist = popupTopicRepository.findByTokenAndTopic(token, topic.getCode(), popup);
        if (topicExist != null){
            popupTopicRepository.delete(topicExist);
        }
        else{
            PopupTopic popupTopic = new PopupTopic(token, popup, topic.getCode());
            popupTopicRepository.save(popupTopic);

            response = firebaseMessaging.subscribeToTopic(registrationTokens, topic.toString());
            log.info(response.getSuccessCount() + " token(s) were subscribed successfully : {}" , token.getToken());
        }
    }

    public void unsubscribePopupTopic(FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException {

        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(token.getToken());

        TopicManagementResponse response = null;
        // 구독 해제
        PopupTopic popupTopic = popupTopicRepository.findByTokenAndTopic(token, topic.getCode(), popup);
        if (popupTopic != null ) popupTopicRepository.delete(popupTopic);
        response = firebaseMessaging.unsubscribeFromTopic(registrationTokens, topic.toString()); // 구독 해제

        log.info(response.getSuccessCount() + " token(s) were unsubscribed successfully");
    }
}
