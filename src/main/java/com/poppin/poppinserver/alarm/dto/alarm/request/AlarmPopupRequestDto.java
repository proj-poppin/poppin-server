package com.poppin.poppinserver.alarm.dto.alarm.request;

public record AlarmPopupRequestDto(

       Long alarmId,
       Long popupId,
       String fcmToken

) {
}
