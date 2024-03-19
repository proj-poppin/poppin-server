package com.poppin.poppinserver.dto.RealTimeVisit.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.RealTimeVisit;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Optional;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RealTimeVisitorsDto(
    @NotNull
    Long userId,

    @NotNull
    Long popupId,

    Optional<Integer> visitorsCnt
) {
    public static RealTimeVisitorsDto fromEntity(RealTimeVisit realTimeVisit , Optional<Integer> realTimeVisitors){
        return RealTimeVisitorsDto.builder()
                .userId(realTimeVisit.getUser().getId())
                .popupId(realTimeVisit.getPopup().getId())
                .visitorsCnt(realTimeVisitors)
                .build();
    }
}
