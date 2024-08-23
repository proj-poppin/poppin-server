package com.poppin.poppinserver.dto.alarm.response;

import lombok.Builder;

@Builder
public record AlarmKeywordResponseDto(
        String keyword
) {
}
