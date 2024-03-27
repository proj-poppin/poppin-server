package com.poppin.poppinserver.dto.popup.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.review.response.ReviewInfoDto;
import com.poppin.poppinserver.dto.visitorData.response.VisitorDataInfoDto;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public record PopupDetailDto(
         Long id,
         String posterUrl,
         String name,
         String introduce,
         String location,
         Integer entranceFee,
         Integer availableAge,
         Boolean parkingAvailable,
         Integer reopenDemandCnt,
         Integer interesteCnt,
         Integer viewCnt,
         String createdAt,
         String editedAt,
         String openDate,
         String closeDate,
         String openTime,
         String closeTime,
         String category,
         String operationStatus,
         List<ReviewInfoDto> review,
         VisitorDataInfoDto visitorData,
         Optional<Integer> realTimeVisit
) {
    public static PopupDetailDto fromEntity(Popup popup, List<ReviewInfoDto> reviewInfoList, VisitorDataInfoDto visitorDataDto, Optional<Integer> realTimeVisit){

        return PopupDetailDto.builder()
                .id(popup.getId())
                .posterUrl(popup.getPosterUrl())
                .name(popup.getName())
                .introduce(popup.getIntroduce())
                .location(popup.getLocation())
                .entranceFee(popup.getEntranceFee())
                .availableAge(popup.getEntranceFee())
                .parkingAvailable(popup.getParkingAvailable())
                .reopenDemandCnt(popup.getReopenDemandCnt())
                .interesteCnt(popup.getInterestCnt())
                .viewCnt(popup.getViewCnt())
                .createdAt(popup.getCreatedAt().toString())
                .editedAt(popup.getEditedAt().toString())
                .openDate(popup.getOpenDate().toString())
                .closeDate(popup.getCloseDate().toString())
                .openTime(popup.getOpenTime().toString())
                .closeTime(popup.getCloseTime().toString())
                .category(popup.getCategory())
                .operationStatus(popup.getOperationStatus())
                .review(reviewInfoList)
                .visitorData(visitorDataDto)
                .realTimeVisit(realTimeVisit)
                .build();
    }
}
