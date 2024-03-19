package com.poppin.poppinserver.dto.RealTimeVisit.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AddVisitorsDto(
        @NotNull
        Long usersId,
        @NotNull
        Long popupId
) {
}
