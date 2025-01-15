package com.poppin.poppinserver.alarm.dto.alarm.response;

import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public record PopupAlarmResponseDto(
        String alarmId,
        String id, // popup id
        String title,
        String body,
        LocalDate createdAt,
        String iconUrl,
        Boolean isRead
) {
    public static PopupAlarmResponseDto fromEntity(PopupAlarm alarm) {

        return PopupAlarmResponseDto.builder()
                .alarmId(String.valueOf(alarm.getId()))
                .id(String.valueOf(alarm.getId()))
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .createdAt(alarm.getCreatedAt().toLocalDate())
                .iconUrl(alarm.getIcon())
                .isRead(alarm.getIsRead())
                .build();
    }
}
