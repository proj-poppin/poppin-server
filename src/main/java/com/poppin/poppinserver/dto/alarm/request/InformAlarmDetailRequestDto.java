package com.poppin.poppinserver.dto.alarm.request;

public record InformAlarmDetailRequestDto(

        String fcmToken,
        Long informId

) {
}
