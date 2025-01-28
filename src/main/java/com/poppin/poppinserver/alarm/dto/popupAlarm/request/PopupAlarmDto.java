package com.poppin.poppinserver.alarm.dto.popupAlarm.request;

import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PopupAlarmDto(

        Popup popup,
        User user,
        String title,
        String body,
        EPopupTopic topic,
        LocalDate createdAt

) {
    public static PopupAlarmDto fromEntity(
            Popup popup,
            User user,
            String title,
            String body,
            EPopupTopic popupTopic
    ){
        return PopupAlarmDto.builder()
                .popup(popup)
                .user(user)
                .title(title)
                .body(body)
                .topic(popupTopic)
                .createdAt(LocalDate.now())
                .build();
    }
}
