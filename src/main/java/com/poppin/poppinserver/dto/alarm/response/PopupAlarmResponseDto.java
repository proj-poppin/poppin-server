package com.poppin.poppinserver.dto.alarm.response;

import com.poppin.poppinserver.domain.PopupAlarm;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public record PopupAlarmResponseDto(

    String title,
    String body,

    LocalDate createdAt,
    String iconUrl
) {
    public static PopupAlarmResponseDto fromEntity(PopupAlarm alarm){

        return PopupAlarmResponseDto.builder()
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .createdAt(alarm.getCreatedAt())
                .iconUrl(alarm.getIcon())
                .build();
    }
}
