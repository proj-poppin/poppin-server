package com.poppin.poppinserver.dto.alarm.response;

import com.poppin.poppinserver.domain.InformAlarm;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record InformAlarmResponseDto(

        String title,
        String body,
        LocalDate createdAt,
        String iconUrl
) {

    public static InformAlarmResponseDto fromEntity(InformAlarm alarm){

        return InformAlarmResponseDto.builder()
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .createdAt(alarm.getCreatedAt())
                .iconUrl(alarm.getIcon())
                .build();
    }
}
