package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
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
        PreferredDto prefered,
        TasteDto taste
) {
    public static List<PopupSearchingDto> fromEntityList(List<Popup> popups, User user) {
        List<PopupSearchingDto> dtoList = new ArrayList<>();

        Set<Interest> interest = user.getInterest();

        List<Long> interestedPopups = new ArrayList<>();
        for (Interest i : interest) {
            interestedPopups.add(i.getPopup().getId());
        }

        for (Popup popup : popups) {
            Boolean isInterested;

            PreferredDto preferredDto = PreferredDto.fromEntity(popup.getPreferedPopup());
            TasteDto tasteDto = TasteDto.fromEntity(popup.getTastePopup());

            if (interestedPopups.contains(popup.getId())) {
                isInterested = Boolean.TRUE;
            } else {
                isInterested = Boolean.FALSE;
            }

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
                            .prefered(preferredDto)
                            .build();

            dtoList.add(popupSummaryDto);
        }

        return dtoList;
    }
}
