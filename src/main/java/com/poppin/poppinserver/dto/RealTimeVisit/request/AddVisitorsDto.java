package com.poppin.poppinserver.dto.RealTimeVisit.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;

@Builder
public record AddVisitorsDto(
        @NotNull
        Long popupId
) {
}
