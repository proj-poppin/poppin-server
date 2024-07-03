package com.poppin.poppinserver.dto.alarm.request;

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
