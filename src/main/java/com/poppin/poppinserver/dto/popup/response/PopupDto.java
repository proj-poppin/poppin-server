package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PosterImage;
import com.poppin.poppinserver.type.EAvailableAge;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record PopupDto(
        Long id,
        String posterUrl,
        String homepageLink,
        String name,
        String introduce,
        String address,
        String addressDetail,
        String entranceFee,
        EAvailableAge availableAge,
        Boolean parkingAvailable,
        Boolean resvRequired,
        Integer reopenDemandCnt,
        Integer interestCnt,
        Integer viewCnt,
        String createdAt,
        String editedAt,
        String openDate,
        String closeDate,
        String openTime,
        String closeTime,
        String operationExcept,
        String operationStatus,
        PreferedDto prefered,
        TasteDto taste,
        List<String> posterList
) {
    public static PopupDto fromEntity(Popup popup){

        // 각 nullable 부분들에 대해 예외처리

        PreferedDto preferedDto = null;
        if (popup.getPreferedPopup() != null){
            preferedDto = PreferedDto.fromEntity(popup.getPreferedPopup());
        }

        String openDate = null;
        if(popup.getOpenDate() != null){
            openDate = popup.getOpenDate().toString();
        }

        String closeDate = null;
        if(popup.getOpenDate() != null){
            closeDate = popup.getCloseDate().toString();
        }

        String openTime = null;
        if(popup.getOpenDate() != null){
            openTime = popup.getOpenTime().toString();
        }

        String closeTime = null;
        if(popup.getOpenDate() != null){
            closeTime = popup.getCloseTime().toString();
        }

        TasteDto tasteDto = TasteDto.fromEntity(popup.getTastePopup());

        List<String> posterList = new ArrayList<>();
        for(PosterImage posterImage : popup.getPosterImages()){
            posterList.add(posterImage.getPosterUrl());
        }

        return PopupDto.builder()
                .id(popup.getId())
                .posterUrl(popup.getPosterUrl())
                .homepageLink(popup.getHomepageLink())
                .name(popup.getName())
                .introduce(popup.getIntroduce())
                .address(popup.getAddress())
                .entranceFee(popup.getEntranceFee())
                .availableAge(popup.getAvailableAge())
                .parkingAvailable(popup.getParkingAvailable())
                .resvRequired(popup.getResvRequired())
                .reopenDemandCnt(popup.getReopenDemandCnt())
                .interestCnt(popup.getInterestCnt())
                .viewCnt(popup.getViewCnt())
                .createdAt(popup.getCreatedAt().toString())
                .editedAt(popup.getEditedAt().toString())
                .openDate(openDate)
                .closeDate(closeDate)
                .openTime(openTime)
                .closeTime(closeTime)
                .operationExcept(popup.getOperationExcept())
                .operationStatus(popup.getOperationStatus())
                .prefered(preferedDto)
                .taste(tasteDto)
                .posterList(posterList)
                .build();
    }
}
