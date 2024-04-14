package com.poppin.poppinserver.dto.visit.response;

import com.poppin.poppinserver.domain.Visit;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Optional;

@Builder
public record RealTimeVisitorsDto(
    @NotNull
    Long userId,

    @NotNull
    Long popupId,

    Optional<Integer> visitorsCnt
) {
    public static RealTimeVisitorsDto fromEntity(Visit visit, Optional<Integer> realTimeVisitors){
        return RealTimeVisitorsDto.builder()
                .userId(visit.getUser().getId())
                .popupId(visit.getPopup().getId())
                .visitorsCnt(realTimeVisitors)
                .build();
    }
}
