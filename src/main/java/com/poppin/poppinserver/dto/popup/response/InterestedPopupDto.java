package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Interest;
import com.poppin.poppinserver.domain.Popup;
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
    public static List<InterestedPopupDto> fromEntityList(Set<Interest> interesteSet){
        List<InterestedPopupDto> dtoList = new ArrayList<>();

        for(Interest intereste : interesteSet){
            Popup popup = intereste.getPopup();
            InterestedPopupDto interestedPopupDto =
                    InterestedPopupDto.builder()
                            .id(popup.getId())
                            .image_url(popup.getPosterUrl()) // 임시 url
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
