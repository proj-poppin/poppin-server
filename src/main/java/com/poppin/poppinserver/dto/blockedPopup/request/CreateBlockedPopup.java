package com.poppin.poppinserver.dto.blockedPopup.request;

import jakarta.validation.constraints.NotNull;

public record CreateBlockedPopup(
        @NotNull Long popupId
) {
}
