package com.poppin.poppinserver.popup.dto.popup.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

/*마이페이지 - 방문인증 팝업*/
@Builder
public record PopupCertiDto(
        @NotNull
        String name,

        @NotNull
        String posterUrl,

        @NotNull
        LocalDateTime visitDate

) {
    public static PopupCertiDto fromEntity(String name, String posterUrl, LocalDateTime visitDate) {
        return PopupCertiDto.builder()
                .name(name)
                .posterUrl(posterUrl)
                .visitDate(visitDate)
                .build();
    }
}
