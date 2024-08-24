package com.poppin.poppinserver.alarm.dto.alarm.response;

import lombok.Builder;

@Builder
public record AlarmKeywordResponseDto(
        String keyword
) {
}
