package com.poppin.poppinserver.inform.dto.managerInform.request;

import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.core.type.EAvailableAge;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateManagerInformDto(
        @NotNull
        String affiliation,
        @NotNull
        String informerEmail,
        String homepageLink,
        String name,
        String introduce,
        String address,
        String addressDetail,
        Boolean entranceRequired,
        String entranceFee,
        EAvailableAge availableAge,
        Boolean parkingAvailable,
        Boolean resvRequired,
        LocalDate openDate,
        LocalDate closeDate,
        LocalTime openTime,
        LocalTime closeTime,
        Double latitude,
        Double longitude,
        String operationExcept){
}
