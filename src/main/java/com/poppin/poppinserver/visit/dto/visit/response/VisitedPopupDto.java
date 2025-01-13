package com.poppin.poppinserver.visit.dto.visit.response;

import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import lombok.Builder;

@Builder
public record VisitedPopupDto(
        PopupStoreDto updatedPopupStore,
        VisitDto newPopupVisit
) {
   public static VisitedPopupDto fromEntity(
           PopupStoreDto popupStoreDto,
           VisitDto visitDto
   ){
       return VisitedPopupDto.builder()
               .updatedPopupStore(popupStoreDto)
               .newPopupVisit(visitDto)
               .build();
   }
}
