package com.poppin.poppinserver.dto.popup.request;

import com.poppin.poppinserver.type.EAvailableAge;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreatePopupDto(
        @NotNull
        String homepageLink,
        @NotNull
        String name,
        @NotNull
        String introduce,
        @NotNull
        String address,
        String addressDetail,
        @NotNull
        String entranceFee,
        @NotNull
        EAvailableAge availableAge,
        @NotNull
        Boolean parkingAvailable,
        @NotNull
        Boolean resvRequired,
        @NotNull
        LocalDate openDate,
        @NotNull
        LocalDate closeDate,
        @NotNull
        LocalTime openTime,
        @NotNull
        LocalTime closeTime,
        String operationExcept,
        @NotNull
        CreatePreferedDto prefered,
        @NotNull
        CreateTasteDto taste,
        @NotNull
        CreateWhoWithDto whoWith
) {

}
