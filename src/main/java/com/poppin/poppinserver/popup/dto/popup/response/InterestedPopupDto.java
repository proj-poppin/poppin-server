package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.domain.Popup;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record InterestedPopupDto(
        String id,
        String imageUrl,
        String status,
        String name,
        String openDate,
        String closeDate
) {
    public static List<InterestedPopupDto> fromEntityList(Set<Interest> interestSet) {
        List<InterestedPopupDto> dtoList = new ArrayList<>();

        for (Interest intereste : interestSet) {
            Popup popup = intereste.getPopup();
            String popupId = String.valueOf(popup.getId());

            InterestedPopupDto interestedPopupDto =
                    InterestedPopupDto.builder()
                            .id(popupId)
                            .imageUrl(popup.getPosterUrl())
                            .status(popup.getOperationStatus())
                            .name(popup.getName())
                            .openDate(popup.getOpenDate().toString())
                            .closeDate(popup.getCloseDate().toString())
                            .build();

            dtoList.add(interestedPopupDto);
        }

        return dtoList;
    }
}
