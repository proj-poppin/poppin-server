package com.poppin.poppinserver.visit.dto.visitorData.response;

import com.poppin.poppinserver.core.type.ECongestionRate;
import com.poppin.poppinserver.visit.domain.VisitorData;
import lombok.Builder;

import java.util.Optional;

@Builder
public record VisitorDataDto(
        VisitorDataDetailDto weekdayAm,
        VisitorDataDetailDto weekdayPm,
        VisitorDataDetailDto weekendAm,
        VisitorDataDetailDto weekendPm,
        Optional<Integer> satisfaction
) {

}
