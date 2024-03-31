package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.type.EAvailableAge;
import lombok.Builder;

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
        Integer interesteCnt,
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
        WhoWithDto whoWith
) {
    public static PopupDto fromEntity(Popup popup){

        PreferedDto preferedDto = PreferedDto.fromEntity(popup.getPreferedPopup());
        TasteDto tasteDto = TasteDto.fromEntity(popup.getTastePopup());
        WhoWithDto whoWithDto = WhoWithDto.fromEntity(popup.getWhoWithPopup());

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
                .interesteCnt(popup.getInterestCnt())
                .viewCnt(popup.getViewCnt())
                .createdAt(popup.getCreatedAt().toString())
                .editedAt(popup.getEditedAt().toString())
                .openDate(popup.getOpenDate().toString())
                .closeDate(popup.getCloseDate().toString())
                .openTime(popup.getOpenTime().toString())
                .closeTime(popup.getCloseTime().toString())
                .operationExcept(popup.getOperationExcept())
                .operationStatus(popup.getOperationStatus())
                .prefered(preferedDto)
                .taste(tasteDto)
                .whoWith(whoWithDto)
                .build();
    }
}
