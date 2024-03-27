package com.poppin.poppinserver.dto.interest.response;

import com.poppin.poppinserver.domain.Interest;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.popup.response.PopupDto;
import com.poppin.poppinserver.dto.user.response.UserDto;
import lombok.Builder;

@Builder
public record InterestDto(
        String createAt,
        UserDto user,
        PopupDto popup
) {
    public static InterestDto fromEntity(Interest intereste, User user, Popup popup){
        return InterestDto.builder()
                .createAt(intereste.getCreatedAt().toString())
                .user(UserDto.fromEntity(user))
                .popup(PopupDto.fromEntity(popup))
                .build();
    }
}
