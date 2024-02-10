package com.poppin.poppinserver.dto.Popup.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
    public static PopupDto fromEntity(Letter letter){
        PopupDto popupDto =
                PopupDto.builder()
                        .title(letter.getTitle())
                        .content(letter.getContent())
                        .created_date(letter.getCreatedDate().toString())
                        .sender_id(letter.getSender().getId())
                        .recipient_id(letter.getRecipient().getId())
                        .letter_id(letter.getId())
                        .build();
        return letterResponseDto;
    }
}
