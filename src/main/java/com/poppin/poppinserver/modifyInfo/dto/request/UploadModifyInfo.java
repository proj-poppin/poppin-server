package com.poppin.poppinserver.modifyInfo.dto.request;

import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.core.type.EAvailableAge;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UploadModifyInfo(
        @NotNull
        Long modifyInfoId,
        @NotNull
        String homepageLink,
        @NotNull
        String name,
        @NotNull
        String introduce,
        @NotNull
        String address,
        @NotNull
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
        @NotNull
        String operationExcept,
        @NotNull
        CreatePreferedDto prefered,
        @NotNull
        CreateTasteDto taste,
        @NotNull
        List<String> keywords
) {
}
