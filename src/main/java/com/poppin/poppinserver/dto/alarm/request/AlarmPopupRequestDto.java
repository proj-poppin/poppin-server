package com.poppin.poppinserver.dto.alarm.request;

public record AlarmPopupRequestDto(

       Long alarmId,
       Long popupId,
       String fcmToken

) {
}
