package com.poppin.poppinserver.popup.dto.blockedPopup.request;

import jakarta.validation.constraints.NotNull;

public record CreateBlockedPopup(
        @NotNull Long popupId
) {
}
