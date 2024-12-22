package com.poppin.poppinserver.alarm.usecase;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.core.annotation.UseCase;

@UseCase
public interface TokenCommandUseCase {

    void applyToken(String token, Long userId);
    void refreshToken(Long userId, String token);
    void removeToken(FCMToken token) throws FirebaseMessagingException;
}
