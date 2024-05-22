package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.type.EOperationStatus;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ManageSearchingDto(
        Long id,
        String name,
        String operationStatus,
        String adminName
) {
    public static List<ManageSearchingDto> fromEntityList(List<Popup> popups){
        List<ManageSearchingDto> dtoList = new ArrayList<>();

        for(Popup popup : popups){

            ManageSearchingDto manageSearchingDto =
                    ManageSearchingDto.builder()
                            .id(popup.getId())
                            .name(popup.getName())
                            .operationStatus(popup.getOperationStatus())
                            .adminName(popup.getAgent().getNickname())
                            .build();

            dtoList.add(manageSearchingDto);
        }

        return dtoList;
    }
}
