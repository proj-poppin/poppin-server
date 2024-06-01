package com.poppin.poppinserver.dto.userInform.request;

import com.poppin.poppinserver.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.dto.popup.request.CreateTasteDto;
import com.poppin.poppinserver.type.EAvailableAge;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UpdateUserInfromDto(
        Long userInformId,
        String homepageLink,
        String name,
        String introduce,
        String address,
        String addressDetail,
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
        String operationExcept,
        CreatePreferedDto prefered,
        CreateTasteDto taste,
        List<String> keywords
) {
}
