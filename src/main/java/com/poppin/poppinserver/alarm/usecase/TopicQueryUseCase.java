package com.poppin.poppinserver.alarm.usecase;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.core.annotation.UseCase;

import java.util.List;

@UseCase
public interface TopicQueryUseCase {
    List<PopupTopic> findPopupTopicByToken(FCMToken token);
}
