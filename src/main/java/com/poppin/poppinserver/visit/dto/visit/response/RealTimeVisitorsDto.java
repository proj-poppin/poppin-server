package com.poppin.poppinserver.visit.dto.visit.response;

import com.poppin.poppinserver.visit.domain.Visit;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Optional;

@Builder
public record RealTimeVisitorsDto(
        @NotNull
        String userId,

        @NotNull
        String popupId,

        Optional<Integer> visitorsCnt
) {
    public static RealTimeVisitorsDto fromEntity(Visit visit, Optional<Integer> realTimeVisitors) {
        return RealTimeVisitorsDto.builder()
                .userId(String.valueOf(visit.getUser().getId()))
                .popupId(String.valueOf(visit.getPopup().getId()))
                .visitorsCnt(realTimeVisitors)
                .build();
    }
}
