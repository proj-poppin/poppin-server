package com.poppin.poppinserver.dto.alarm.response;

import lombok.Builder;

@Builder
public record UnreadAlarmResponseDto(

        Boolean alarmStatus

) {
    public static UnreadAlarmResponseDto fromEntity(Boolean alarmStatus){

        return UnreadAlarmResponseDto.builder()
                .alarmStatus(alarmStatus)
                .build();
    }
}
