package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.visit.dto.visit.response.VisitDto;
import java.util.List;
import lombok.Builder;

@Builder
public record PopupActivityResponseDto(
        List<PopupScrapDto> scrappedPopups,
        List<VisitDto> visitedPopups,
        List<PopupWaitingDto> waitingPopups
) {
    public static PopupActivityResponseDto fromProperties(
            List<PopupScrapDto> scrappedPopups,
            List<VisitDto> visitedPopups,
            List<PopupWaitingDto> waitingPopups
    ) {
        return PopupActivityResponseDto.builder()
                .scrappedPopups(scrappedPopups)
                .visitedPopups(visitedPopups)
                .waitingPopups(waitingPopups)
                .build();
    }
}
