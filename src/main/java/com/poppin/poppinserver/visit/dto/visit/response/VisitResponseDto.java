package com.poppin.poppinserver.visit.dto.visit.response;

import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import lombok.Builder;

@Builder
public record VisitResponseDto(
        PopupStoreDto updatedPopupStore,
        VisitDto newPopupVisit
) {
   public static VisitResponseDto fromEntity(
           PopupStoreDto popupStoreDto,
           VisitDto visitDto
   ){
       return VisitResponseDto.builder()
               .updatedPopupStore(popupStoreDto)
               .newPopupVisit(visitDto)
               .build();
   }
}
