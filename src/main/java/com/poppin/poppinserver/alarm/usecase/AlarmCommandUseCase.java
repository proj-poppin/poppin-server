package com.poppin.poppinserver.alarm.usecase;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.popupAlarm.request.PopupAlarmDto;
import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.core.type.EPopupTopic;

import java.net.URL;

@UseCase
public interface AlarmCommandUseCase {

    InformAlarm insertInformAlarm(InformAlarmCreateRequestDto requestDto);
    String insertPopupAlarm(PopupAlarmDto popupAlarmDto);
    void insertInformIsRead(FCMToken token, InformAlarm informAlarm);
    URL getUrlForTopic(EPopupTopic topic);
}
