package com.poppin.poppinserver.dto.report.response;

import lombok.Builder;

@Builder
public record ReportedPopupDetailDto(
        Long popupId,
        String popupName,
        String introduce,
        String posterUrl,
        String homepageLink,
        String openDate,
        String closeDate,
        String openTime,
        String closeTime,
        String address,
        String addressDetail,
        String entranceFee,
        String availableAge,
        Boolean parkingAvailable,   // 주차 가능 여부 -> String or Boolean?
        Boolean resvRequired
) {
}
