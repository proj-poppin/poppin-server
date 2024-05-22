package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Interest;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.type.EOperationStatus;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
public record PopupSearchingDto(
        Long id,
        String posterUrl,
        String name,
        String address,
        Integer viewCnt,
        String createdAt,
        String editedAt,
        String openDate,
        String closeDate,
        String operationStatus,
        Boolean isInterested,
        PreferedDto prefered,
        TasteDto taste
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

            PreferedDto preferedDto = PreferedDto.fromEntity(popup.getPreferedPopup());
            TasteDto tasteDto = TasteDto.fromEntity(popup.getTastePopup());

            if(interestedPopups.contains(popup)) isInterested = Boolean.TRUE;
            else isInterested = Boolean.FALSE;

            PopupSearchingDto popupSummaryDto =
                    PopupSearchingDto.builder()
                            .id(popup.getId())
                            .posterUrl(popup.getPosterUrl())
                            .name(popup.getName())
                            .address(popup.getAddress())
                            .viewCnt(popup.getViewCnt())
                            .createdAt(popup.getCreatedAt().toString())
                            .editedAt(popup.getEditedAt().toString())
                            .openDate(popup.getOpenDate().toString())
                            .closeDate(popup.getCloseDate().toString())
                            .operationStatus(popup.getOperationStatus())
                            .isInterested(isInterested)
                            .taste(tasteDto)
                            .prefered(preferedDto)
                            .build();

            dtoList.add(popupSummaryDto);
        }

        return dtoList;
    }
}
