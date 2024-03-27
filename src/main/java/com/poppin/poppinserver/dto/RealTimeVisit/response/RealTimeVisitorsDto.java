package com.poppin.poppinserver.dto.RealTimeVisit.response;

import com.poppin.poppinserver.domain.RealTimeVisit;
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
    public static RealTimeVisitorsDto fromEntity(RealTimeVisit realTimeVisit , Optional<Integer> realTimeVisitors){
        return RealTimeVisitorsDto.builder()
                .userId(realTimeVisit.getUser().getId())
                .popupId(realTimeVisit.getPopup().getId())
                .visitorsCnt(realTimeVisitors)
                .build();
    }
}
