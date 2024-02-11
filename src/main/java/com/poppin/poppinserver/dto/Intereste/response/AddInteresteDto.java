package com.poppin.poppinserver.dto.Intereste.response;

import com.poppin.poppinserver.dto.Popup.response.PopupDto;
import com.poppin.poppinserver.dto.User.respnse.UserDto;

public record AddInteresteDto(
        UserDto user,
        PopupDto popup
) {
}
