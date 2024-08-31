package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.interest.domain.Interest;
import com.poppin.poppinserver.popup.domain.Popup;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Builder
public record InterestedPopupDto(
        Long id,
        String image_url,
        String status,
        String name,
        String open_date,
        String close_date
) {
    public static List<InterestedPopupDto> fromEntityList(Set<Interest> interestSet){
        List<InterestedPopupDto> dtoList = new ArrayList<>();

        for(Interest intereste : interestSet){
            Popup popup = intereste.getPopup();
            InterestedPopupDto interestedPopupDto =
                    InterestedPopupDto.builder()
                            .id(popup.getId())
                            .image_url(popup.getPosterUrl())
                            .status(popup.getOperationStatus())
                            .name(popup.getName())
                            .open_date(popup.getOpenDate().toString())
                            .close_date(popup.getCloseDate().toString())
                            .build();

            dtoList.add(interestedPopupDto);
        }

        return dtoList;
    }
}
