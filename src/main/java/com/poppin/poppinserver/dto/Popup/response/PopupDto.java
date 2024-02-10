package com.poppin.poppinserver.dto.Popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Popup;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PopupDto(
        Long id,
        String posterUrl,
        String name,
        String introduce,
        String location,
        Integer entranceFee,
        Integer availableAge,
        Boolean parkingAvailable,
        Integer visiterCnt,
        Integer reopenDemandCnt,
        Integer interestCnt,
        Integer viewCnt,
        LocalDateTime createdAt,
        LocalDateTime editedAt,
        LocalDate openDate,
        LocalDate closeDate,
        LocalTime openTime,
        LocalTime closeTime,
        String category,
        String operationStatus
) {
    public static PopupDto fromEntity(Popup popup){
        return PopupDto.builder()
                .id(popup.getId())
                .posterUrl(popup.getPosterUrl())
                .name(popup.getName())
                .introduce(popup.getIntroduce())
                .location(popup.getLocation())
                .entranceFee(popup.getEntranceFee())
                .availableAge(popup.getEntranceFee())
                .parkingAvailable(popup.getParkingAvailable())
                .visiterCnt(popup.getVisiterCnt())
                .reopenDemandCnt(popup.getReopenDemandCnt())
                .interestCnt(popup.getInterestCnt())
                .viewCnt(popup.getViewCnt())
                .createdAt(popup.getCreatedAt())
                .editedAt(popup.getEditedAt())
                .openDate(popup.getOpenDate())
                .closeDate(popup.getCloseDate())
                .openTime(popup.getOpenTime())
                .closeTime(popup.getCloseTime())
                .category(popup.getCategory())
                .operationStatus(popup.getOperationStatus())
                .build();
    }
}
