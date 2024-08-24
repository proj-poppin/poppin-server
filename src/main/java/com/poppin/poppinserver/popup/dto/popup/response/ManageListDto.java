package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.popup.domain.Popup;
import lombok.Builder;

import java.util.List;

@Builder
public record ManageListDto(
    List<ManageSummaryDto> popups,
    Long popupNum
) {
    public static ManageListDto fromEntityList(List<Popup> popups, Long num){
        return ManageListDto.builder()
                .popups(ManageSummaryDto.fromEntityList(popups))
                .popupNum(num)
                .build();
    }
}
