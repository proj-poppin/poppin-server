package com.poppin.poppinserver.alarm.dto.alarm.response;

import lombok.Builder;

@Builder
public record AlarmStatusResponseDto(

        Boolean alarmStatus

) {
    public static AlarmStatusResponseDto fromEntity(Boolean alarmStatus){

        return AlarmStatusResponseDto.builder()
                .alarmStatus(alarmStatus)
                .build();
    }
}
