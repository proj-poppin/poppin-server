package com.poppin.poppinserver.alarm.dto.alarm.response;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import lombok.Builder;

import java.time.LocalDate;

// 1 depth
@Builder
public record InformAlarmListResponseDto(
        String id, // 공지사항 id
        String title,
        String body,
        LocalDate createdAt,
        String iconUrl,
        Boolean isRead
) {

    public static InformAlarmListResponseDto fromEntity(InformAlarm alarm, Boolean isRead) {

        return InformAlarmListResponseDto.builder()
                .id(String.valueOf(alarm.getId()))
                .title(alarm.getTitle())
                .body(alarm.getBody())
                .createdAt(alarm.getCreatedAt().toLocalDate())
                .iconUrl(alarm.getIcon())
                .isRead(isRead)
                .build();
    }
}
