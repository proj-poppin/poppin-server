package com.poppin.poppinserver.alarm.dto.alarm.request;

import lombok.Builder;

@Builder
public record SettingRequestDto(

        String fcmToken,
        String pushYn,
        String pushNightYn,
        String hoogiYn,
        String openYn,
        String magamYn,
        String changeInfoYn
) {
}
