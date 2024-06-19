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
        Boolean parkingAvailable,
        Boolean resvRequired,
        // 위에는 팝업에 관한 정보, 아래는 신고에 관한 정보
        String reporter,
        String reportedAt,
        String content
) {
}
