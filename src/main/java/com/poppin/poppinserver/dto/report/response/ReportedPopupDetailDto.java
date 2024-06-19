package com.poppin.poppinserver.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedPopupDetailDto(
        Long popupId,
        String popupName,
        String posterUrl,
        String homepageLink,
        String address,
        String addressDetail,
        String entranceFee,
        String availableAge,
        Boolean parkingAvailable,   // 주차 가능 여부 -> String or Boolean?
        Boolean resvRequired
) {
}
