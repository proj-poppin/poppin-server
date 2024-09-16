package com.poppin.poppinserver.alarm.dto.alarm.response;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import lombok.Builder;

@Builder
public record SettingResponseDto(
        String fcmToken,
        Boolean pushYn,
        Boolean pushNightYn,
        Boolean hoogiYn,
        Boolean openYn,
        Boolean magamYn,
        Boolean changeInfoYn
) {

    public static SettingResponseDto fromEntity(AlarmSetting setting) {
        return SettingResponseDto.builder()
                .fcmToken(setting.getToken())
                .pushYn(setting.getPushYn())
                .pushNightYn(setting.getPushNightYn())
                .hoogiYn(setting.getHoogiYn())
                .openYn(setting.getOpenYn())
                .magamYn(setting.getMagamYn())
                .changeInfoYn(setting.getChangeInfoYn())
                .build();
    }
}
