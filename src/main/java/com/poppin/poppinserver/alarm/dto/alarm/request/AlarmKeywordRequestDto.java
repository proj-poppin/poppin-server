package com.poppin.poppinserver.alarm.dto.alarm.request;

import jakarta.validation.constraints.NotNull;

public record AlarmKeywordRequestDto(
        @NotNull String keyword
) {
}
