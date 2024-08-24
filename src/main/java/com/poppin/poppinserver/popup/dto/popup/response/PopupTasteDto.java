package com.poppin.poppinserver.popup.dto.popup.response;

import java.util.List;

public record PopupTasteDto(
        String taste,
        List<PopupSummaryDto> popupSummaryDtos
) {
}
