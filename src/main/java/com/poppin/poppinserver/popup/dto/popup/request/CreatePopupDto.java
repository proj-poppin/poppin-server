package com.poppin.poppinserver.popup.dto.popup.request;

import com.poppin.poppinserver.core.type.EAvailableAge;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
        Boolean entranceRequired,
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

        @NotNull
        Double latitude,

        @NotNull
        Double longitude,

        String operationExcept,
        @NotNull
        CreatePreferedDto prefered,
        @NotNull
        CreateTasteDto taste,
        @NotNull
        List<String> keywords
) {

}
