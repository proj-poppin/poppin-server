package com.poppin.poppinserver.alarm.dto.popupAlarm.request;

import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PopupAlarmDto(

        Popup popupId,
        String fcmToken,
        String title,
        String body,
        EPopupTopic topic,
        LocalDate createdAt,
        Boolean isRead

) {
    public static PopupAlarmDto fromEntity(
            Popup popup,
            String fcmToken,
            String title,
            String body,
            EPopupTopic popupTopic
    ){
        return PopupAlarmDto.builder()
                .popupId(popup)
                .fcmToken(fcmToken)
                .title(title)
                .body(body)
                .topic(popupTopic)
                .createdAt(LocalDate.now())
                .isRead(false)
                .build();
    }
}
