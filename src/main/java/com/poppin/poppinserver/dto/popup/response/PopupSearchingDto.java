package com.poppin.poppinserver.dto.popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Interest;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PopupSearchingDto(
        Long id,
        String posterUrl,
        String name,
        String location,
        Integer viewCnt,
        String createdAt,
        String editedAt,
        String openDate,
        String closeDate,
        String category,
        String operationStatus,
        Boolean isInterested
) {
    public static List<PopupSearchingDto> fromEntityList(List<Popup> popups, User user){
        List<PopupSearchingDto> dtoList = new ArrayList<>();

        Set<Interest> interestes = user.getInterestes();

        List<Popup> interestedPopups = new ArrayList<>();
        for(Interest intereste : interestes){
            interestedPopups.add(intereste.getPopup());
        }

        for(Popup popup : popups){
            Boolean isInterested;

            if(interestedPopups.contains(popup)) isInterested = Boolean.TRUE;
            else isInterested = Boolean.FALSE;

            PopupSearchingDto popupSummaryDto =
                    PopupSearchingDto.builder()
                            .id(popup.getId())
                            .posterUrl(popup.getPosterUrl())
                            .name(popup.getName())
                            .location(popup.getLocation())
                            .viewCnt(popup.getViewCnt())
                            .createdAt(popup.getCreatedAt().toString())
                            .editedAt(popup.getEditedAt().toString())
                            .openDate(popup.getOpenDate().toString())
                            .closeDate(popup.getCloseDate().toString())
                            .category(popup.getCategory())
                            .operationStatus(popup.getOperationStatus())
                            .isInterested(isInterested)
                            .build();

            dtoList.add(popupSummaryDto);
        }

        return dtoList;
    }
}
