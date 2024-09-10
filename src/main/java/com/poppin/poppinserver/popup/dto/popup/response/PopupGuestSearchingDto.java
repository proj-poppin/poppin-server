package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.popup.domain.Popup;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record PopupGuestSearchingDto(
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
        PreferedDto prefered,
        TasteDto taste
) {
    public static List<PopupGuestSearchingDto> fromEntityList(List<Popup> popups) {
        List<PopupGuestSearchingDto> dtoList = new ArrayList<>();

        for (Popup popup : popups) {

            PreferedDto preferedDto = PreferedDto.fromEntity(popup.getPreferedPopup());
            TasteDto tasteDto = TasteDto.fromEntity(popup.getTastePopup());

            PopupGuestSearchingDto popupGuestSearchingDto =
                    PopupGuestSearchingDto.builder()
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
                            .taste(tasteDto)
                            .prefered(preferedDto)
                            .build();

            dtoList.add(popupGuestSearchingDto);
        }

        return dtoList;
    }
}
