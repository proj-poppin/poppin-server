package com.poppin.poppinserver.alarm.usecase;

import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.user.domain.User;

import java.util.List;

@UseCase
public interface TopicQueryUseCase {
    List<PopupTopic> findPopupTopicByUser(User user);
}
