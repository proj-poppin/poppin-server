package com.poppin.poppinserver.popup.dto.popup.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record VisitedPopupDto(

        @NotNull
        String popupId,

        @NotNull
        String name,

        @NotNull
        String posterUrl,

        @NotNull
        LocalDateTime visitDate

) {
    public static VisitedPopupDto fromEntity(String id, String name, String posterUrl, LocalDateTime visitDate) {
        return VisitedPopupDto.builder()
                .popupId(id)
                .name(name)
                .posterUrl(posterUrl)
                .visitDate(visitDate)
                .build();
    }
}