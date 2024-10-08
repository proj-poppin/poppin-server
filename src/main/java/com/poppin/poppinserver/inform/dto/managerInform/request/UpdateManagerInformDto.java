package com.poppin.poppinserver.inform.dto.managerInform.request;

import com.poppin.poppinserver.core.type.EAvailableAge;
import com.poppin.poppinserver.popup.dto.popup.request.CreatePreferedDto;
import com.poppin.poppinserver.popup.dto.popup.request.CreateTasteDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UpdateManagerInformDto(
        String affiliation,
        String informerEmail,
        String managerInformId,
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
        String operationExcept,
        CreatePreferedDto prefered,
        CreateTasteDto taste,
        List<String> keywords
) {
}
