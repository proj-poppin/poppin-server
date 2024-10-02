package com.poppin.poppinserver.visit.dto.visit.response;

import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import lombok.Builder;

@Builder
public record VisitResponseDto(
        PopupStoreDto popupStoreDto,
        VisitStatusDto visitStatusDto
) {
   public static VisitResponseDto fromEntity(
           PopupStoreDto popupStoreDto,
           VisitStatusDto visitStatusDto
   ){
       return VisitResponseDto.builder()
               .popupStoreDto(popupStoreDto)
               .visitStatusDto(visitStatusDto)
               .build();
   }
}
