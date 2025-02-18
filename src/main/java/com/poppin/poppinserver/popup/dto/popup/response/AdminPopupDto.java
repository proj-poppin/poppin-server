package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record AdminPopupDto(
        Long id,
        String posterUrl,
        String homepageLink,
        String name,
        String introduce,
        String address,
        String addressDetail,
        Boolean entranceRequired,
        String entranceFee,
        String availableAge,
        String availableAgeValue,
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
        PreferredDto prefered,
        TasteDto taste,
        List<String> posterList,
        List<String> keywordList
) {
    public static AdminPopupDto fromEntity(Popup popup) {

        // 각 nullable 부분들에 대해 예외처리

        PreferredDto preferredDto = null;
        if (popup.getPreferedPopup() != null) {
            preferredDto = PreferredDto.fromEntity(popup.getPreferedPopup());
        }

        String openDate = null;
        if (popup.getOpenDate() != null) {
            openDate = popup.getOpenDate().toString();
        }

        String closeDate = null;
        if (popup.getOpenDate() != null) {
            closeDate = popup.getCloseDate().toString();
        }

        String openTime = null;
        if (popup.getOpenDate() != null) {
            openTime = popup.getOpenTime().toString();
        }

        String closeTime = null;
        if (popup.getOpenDate() != null) {
            closeTime = popup.getCloseTime().toString();
        }

        TasteDto tasteDto = TasteDto.fromEntity(popup.getTastePopup());

        List<String> posterList = new ArrayList<>();
        for (PosterImage posterImage : popup.getPosterImages()) {
            posterList.add(posterImage.getPosterUrl());
        }

        List<String> keywordList = new ArrayList<>();
        for (PopupAlarmKeyword popupAlarmKeyword : popup.getPopupAlarmKeywords()) {
            keywordList.add(popupAlarmKeyword.getKeyword());
        }

        String availableAge = null;
        String availableAgeValue = null;
        if (popup.getAvailableAge() != null) {
            availableAge = popup.getAvailableAge().getAvailableAgeProvider();
            availableAgeValue = popup.getAvailableAge().toString();
        }

        String entranceFee = null;
        if (popup.getEntranceRequired() != null && popup.getEntranceRequired()) {
            entranceFee = popup.getEntranceFee();
        } else {
            entranceFee = "0";
        }

        return AdminPopupDto.builder()
                .id(popup.getId())
                .posterUrl(popup.getPosterUrl())
                .homepageLink(popup.getHomepageLink())
                .name(popup.getName())
                .introduce(popup.getIntroduce())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
                .entranceRequired(popup.getEntranceRequired())
                .entranceFee(entranceFee)
                .availableAge(availableAge)
                .availableAgeValue(availableAgeValue)
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
                .prefered(preferredDto)
                .taste(tasteDto)
                .posterList(posterList)
                .keywordList(keywordList)
                .build();
    }
}
