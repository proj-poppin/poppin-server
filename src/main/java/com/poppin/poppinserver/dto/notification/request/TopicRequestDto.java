package com.poppin.poppinserver.dto.notification.request;

import com.poppin.poppinserver.type.EPushInfo;
import jakarta.validation.constraints.NotNull;


public record TopicRequestDto(
        @NotNull
        String fcmToken,        // 토큰
        @NotNull
        EPushInfo popupInfo    // 알림 제목, 내용

) {
}
