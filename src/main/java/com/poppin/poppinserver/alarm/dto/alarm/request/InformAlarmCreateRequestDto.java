package com.poppin.poppinserver.alarm.dto.alarm.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record InformAlarmCreateRequestDto(

        @NotNull
        String title,

        @NotNull
        String body

) {
}
