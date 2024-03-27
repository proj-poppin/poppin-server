package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Popup;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record PopupGuestSearchingDto(
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
        PreferedDto prefered,
        TasteDto taste,
        WhoWithDto whoWith
) {
    public static List<PopupGuestSearchingDto> fromEntityList(List<Popup> popups){
        List<PopupGuestSearchingDto> dtoList = new ArrayList<>();

        for(Popup popup : popups){

            PreferedDto preferedDto = PreferedDto.fromEntity(popup.getPreferedPopup());
            TasteDto tasteDto = TasteDto.fromEntity(popup.getTastePopup());
            WhoWithDto whoWithDto = WhoWithDto.fromEntity(popup.getWhoWithPopup());

            PopupGuestSearchingDto popupGuestSearchingDto =
                    PopupGuestSearchingDto.builder()
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
                            .taste(tasteDto)
                            .prefered(preferedDto)
                            .whoWith(whoWithDto)
                            .build();

            dtoList.add(popupGuestSearchingDto);
        }

        return dtoList;
    }
}
