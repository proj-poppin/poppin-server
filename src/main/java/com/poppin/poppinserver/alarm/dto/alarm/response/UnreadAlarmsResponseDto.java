package com.poppin.poppinserver.alarm.dto.alarm.response;

import lombok.Builder;

@Builder
public record UnreadAlarmsResponseDto(
        int unread
) {
    public static UnreadAlarmsResponseDto fromEntity(int count) {
        return UnreadAlarmsResponseDto.builder()
                .unread(count)
                .build();
    }
}
