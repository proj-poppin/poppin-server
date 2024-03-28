package com.poppin.poppinserver.dto.popup.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreatePopupDto(
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
        CreatePreferedDto prefered,
        @NotNull
        CreateTasteDto taste,
        @NotNull
        CreateWhoWithDto whoWith
) {

}
