package com.poppin.poppinserver.dto.popup.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CreatePopupDto(
        String posterUrl,
        @NotNull
        String name,
        @NotNull
        String introduce,
        @NotNull
        String location,
        @NotNull
        Integer entranceFee,
        @NotNull
        Integer availableAge,
        @NotNull
        Boolean parkingAvailable,
        @NotNull
        LocalDate openDate,
        @NotNull
        LocalDate closeDate,
        @NotNull
        LocalTime openTime,
        @NotNull
        LocalTime closeTime,
        @NotNull
        String category,
        @NotNull
        String operationStatus
) {

}
