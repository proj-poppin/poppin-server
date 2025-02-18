package com.poppin.poppinserver.alarm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.alarm.repository.PopupTopicRepository;
import com.poppin.poppinserver.alarm.usecase.TopicCommandUseCase;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TopicCommandService implements TopicCommandUseCase {

    private final PopupTopicRepository popupTopicRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void subscribePopupTopic(User user, FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException {
        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(token.getToken());

        TopicManagementResponse response = null;

        log.info("subscribe start");

        PopupTopic topicExist = popupTopicRepository.findPopupTopicByTopicCode(user, topic.getCode(), popup);
        if (topicExist == null) {
            PopupTopic popupTopic = new PopupTopic(user, popup, topic.getCode());
            popupTopicRepository.save(popupTopic); // 데이터 저장

            response = firebaseMessaging.subscribeToTopic(registrationTokens, topic.toString()); // 구독
            log.info(response.getSuccessCount() + " token(s) were subscribed successfully : {}", token.getToken());
        } else {
            log.info(registrationTokens + "already subscribed");
        }
    }

    @Override
    public void unsubscribePopupTopic(User user, FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException {
        List<String> registrationTokens = new ArrayList<>();
        registrationTokens.add(token.getToken());

        TopicManagementResponse response = null;

        PopupTopic topicExist = popupTopicRepository.findPopupTopicByTopicCode(user, topic.getCode(), popup);
        if (topicExist != null) {
            popupTopicRepository.delete(topicExist);
        }
        response = firebaseMessaging.unsubscribeFromTopic(registrationTokens, topic.toString()); // 구독 해제

        log.info(response.getSuccessCount() + " token(s) were unsubscribed successfully");
    }

    @Override
    public void delete(PopupTopic topic) {
        popupTopicRepository.delete(topic);
    }
}
