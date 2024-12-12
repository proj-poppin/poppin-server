package com.poppin.poppinserver.alarm.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;

@UseCase
public interface AlarmListQueryUseCase {

    int countUnreadAlarms(String fcmToken);



}
