package com.poppin.poppinserver.visit.dto.visitorData.response;

import com.poppin.poppinserver.core.type.ECongestionRate;
import lombok.Builder;

@Builder
public record VisitorDataDetailDto(
        Integer congestionRatio,
        ECongestionRate congestionRate
) {
}
