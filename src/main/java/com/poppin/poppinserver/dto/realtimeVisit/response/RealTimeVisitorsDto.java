package com.poppin.poppinserver.dto.realtimeVisit.response;

import com.poppin.poppinserver.domain.Visitor;
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
    public static RealTimeVisitorsDto fromEntity(Visitor visitor, Optional<Integer> realTimeVisitors){
        return RealTimeVisitorsDto.builder()
                .userId(visitor.getUser().getId())
                .popupId(visitor.getPopup().getId())
                .visitorsCnt(realTimeVisitors)
                .build();
    }
}
