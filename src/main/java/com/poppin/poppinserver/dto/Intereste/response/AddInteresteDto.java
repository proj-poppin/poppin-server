package com.poppin.poppinserver.dto.Intereste.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.dto.Popup.response.PopupDto;
import com.poppin.poppinserver.dto.User.respnse.UserDto;
import lombok.Builder;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AddInteresteDto(
        String createAt,
        UserDto user,
        PopupDto popup
) {
}
