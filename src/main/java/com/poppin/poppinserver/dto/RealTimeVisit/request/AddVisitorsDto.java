package com.poppin.poppinserver.dto.RealTimeVisit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AddVisitorsDto(
        @NotNull
        Long userId,
        @NotNull
        Long popupId
) {
}
