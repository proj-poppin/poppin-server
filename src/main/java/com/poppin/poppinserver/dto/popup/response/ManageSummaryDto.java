package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.type.EOperationStatus;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ManageSummaryDto(
        Long id,
        String name,
        String operationStatus,
        String adminName,
        String createdAt
) {
    public static List<ManageSummaryDto> fromEntityList(List<Popup> popups){
        List<ManageSummaryDto> dtoList = new ArrayList<>();

        for(Popup popup : popups){
            ManageSummaryDto manageSummaryDto =
                    ManageSummaryDto.builder()
                            .id(popup.getId())
                            .name(popup.getName())
                            .operationStatus(popup.getOperationStatus())
                            .adminName(popup.getAgent().getNickname())
                            .createdAt(popup.getCreatedAt().toString())
                            .build();

            dtoList.add(manageSummaryDto);
        }

        return dtoList;
    }
}
