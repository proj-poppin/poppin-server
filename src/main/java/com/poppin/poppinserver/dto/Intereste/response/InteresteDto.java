package com.poppin.poppinserver.dto.Intereste.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.poppin.poppinserver.domain.Intereste;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.Popup.response.PopupDto;
import com.poppin.poppinserver.dto.User.respnse.UserDto;
import lombok.Builder;

@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record InteresteDto(
        String createAt,
        UserDto user,
        PopupDto popup
) {
    public static InteresteDto fromEntity(Intereste intereste, User user, Popup popup){
        return InteresteDto.builder()
                .createAt(intereste.getCreatedAt().toString())
                .user(UserDto.fromEntity(user))
                .popup(PopupDto.fromEntity(popup))
                .build();
    }
}
