package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.review.response.ReviewInfoDto;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataInfoDto;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public record PopupGuestDetailDto(
         Long id,
         String homepageLink,
         Boolean isInstagram,
         String name,
         String introduce,
         String address,
         String addressDetail,
         String entranceFee,
         String availableAge,
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
         Double latitude,
         Double longitude,
         String operationExcept,
         String operationStatus,
         List<String> images,
         List<ReviewInfoDto> review,
         VisitorDataInfoDto visitorData,
         Optional<Integer> realTimeVisit,
         Boolean isVisited
) {
    public static PopupGuestDetailDto fromEntity(Popup popup, List<String> images, List<ReviewInfoDto> reviewInfoList, VisitorDataInfoDto visitorDataDto, Optional<Integer> realTimeVisit){

        Boolean isInstagram = popup.getHomepageLink().contains("instagram");

        return PopupGuestDetailDto.builder()
                .id(popup.getId())
                .homepageLink(popup.getHomepageLink())
                .isInstagram(isInstagram)
                .name(popup.getName())
                .introduce(popup.getIntroduce())
                .address(popup.getAddress())
                .addressDetail(popup.getAddressDetail())
                .entranceFee(popup.getEntranceFee())
                .availableAge(popup.getAvailableAge().getAvailableAgeProvider())
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
                .latitude(popup.getLatitude())
                .longitude(popup.getLongitude())
                .operationExcept(popup.getOperationExcept())
                .operationStatus(popup.getOperationStatus())
                .images(images)
                .review(reviewInfoList)
                .visitorData(visitorDataDto)
                .realTimeVisit(realTimeVisit)
                .isVisited(true)
                .build();
    }
}
