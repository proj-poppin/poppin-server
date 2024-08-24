package com.poppin.poppinserver.alarm.dto.alarm.response;

import lombok.Builder;

import java.time.LocalDate;

// 2 depth
@Builder
public record InformAlarmResponseDto(
        Long id,
        String title,
        String body,
        String posterUrl, // 알림 포스터
        LocalDate createdAt
) {

    public static InformAlarmResponseDto fromEntity(
            Long id,
            String title,
            String body,
            String posterUrl,
            LocalDate createdAt
    ){
        return InformAlarmResponseDto.builder().
                id(id)
                .title(title)
                .body(body)
                .posterUrl(posterUrl)
                .createdAt(createdAt)
                .build();
    }

}
