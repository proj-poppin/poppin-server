package com.poppin.poppinserver.dto.alarm.response;

import com.poppin.poppinserver.domain.PopupAlarm;
import lombok.Builder;

import java.time.LocalDate;


@Builder
public record PopupAlarmResponseDto(
    Long alarmId,
    Long id, // popup id
    String title,
    String body,
    LocalDate createdAt,
    String iconUrl,
    Boolean isRead
) {
    public static PopupAlarmResponseDto fromEntity(PopupAlarm alarm){

        return PopupAlarmResponseDto.builder()
                .alarmId(alarm.getId())
                .id(alarm.getPopupId().getId())
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .createdAt(alarm.getCreatedAt())
                .iconUrl(alarm.getIcon())
                .isRead(alarm.getIsRead())
                .build();
    }
}
