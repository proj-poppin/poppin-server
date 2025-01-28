package com.poppin.poppinserver.alarm.usecase;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;

@UseCase
public interface TopicCommandUseCase {

    void subscribePopupTopic(User user, FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException;
    void unsubscribePopupTopic(User user, FCMToken token, Popup popup, EPopupTopic topic) throws FirebaseMessagingException;

    void delete(PopupTopic topic);
}
