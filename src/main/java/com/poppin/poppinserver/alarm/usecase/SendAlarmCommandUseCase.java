package com.poppin.poppinserver.alarm.usecase;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmKeywordCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.fcm.request.FCMRequestDto;
import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.user.domain.User;

import java.util.List;

@UseCase
public interface SendAlarmCommandUseCase {

    void sendInformationAlarm(List<FCMToken> tokenList,
                              InformAlarmCreateRequestDto requestDto,
                              InformAlarm informAlarm);

    void sendScheduledPopupAlarm(List<Popup> popupList,
                                 EPushInfo info);

    void sendChoochunAlarm(
                           User user,
                           Popup popup,
                           Review review,
                           EPushInfo info);

    void sendKeywordAlarm(FCMToken token,
                          AlarmKeywordCreateRequestDto requestDto,
                          UserAlarmKeyword userAlarmKeyword);

    void sendPopupTopicAlarm(List<FCMRequestDto> fcmRequestDtoList);


}
