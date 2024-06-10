package com.poppin.poppinserver.dto.alarm.response;

import com.poppin.poppinserver.domain.Alarm;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record NotifyAlarmResponseDto(

        String title,
        String body,

        String detailUrl, // 공지사항 상세

        LocalDate createdAt,
        String iconUrl
) {
    public static NotifyAlarmResponseDto fromEntity(Alarm alarm){

        return NotifyAlarmResponseDto.builder()
                .title(alarm.getTitle())
                .body(alarm.getBody())
//                .detailUrl() // 공지사항 url
                .createdAt(alarm.getCreatedAt())
                .iconUrl(alarm.getUrl())
                .build();
    }
}
