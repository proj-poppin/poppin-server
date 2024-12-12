package com.poppin.poppinserver.alarm.usecase;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;

@UseCase
public interface TopicCommandUseCase {

    void subscribePopupTopic(FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException;
    void unsubscribePopupTopic(FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException;

    void delete(PopupTopic topic);
}
