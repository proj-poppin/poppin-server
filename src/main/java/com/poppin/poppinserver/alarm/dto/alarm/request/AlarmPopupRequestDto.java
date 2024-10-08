package com.poppin.poppinserver.alarm.dto.alarm.request;

public record AlarmPopupRequestDto(

        String alarmId,
        String popupId,
        String fcmToken

) {
}
