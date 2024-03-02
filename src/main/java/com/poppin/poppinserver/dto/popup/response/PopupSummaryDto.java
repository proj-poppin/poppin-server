package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Popup;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PopupSummaryDto(
        @NotNull
        Long id,
        @NotNull
        String image_url,
        @NotNull
        String name,
        @NotNull
        String introduce
) {
    public static List<PopupSummaryDto> fromEntityList(List<Popup> popups){
        List<PopupSummaryDto> dtoList = new ArrayList<>();

        for(Popup popup : popups){
            PopupSummaryDto popupSummaryDto =
                    PopupSummaryDto.builder()
                            .id(popup.getId())
                            .image_url(popup.getPosterUrl())
                            .name(popup.getName())
                            .introduce(popup.getIntroduce())
                            .build();

            dtoList.add(popupSummaryDto);
        }

        return dtoList;
    }
}
