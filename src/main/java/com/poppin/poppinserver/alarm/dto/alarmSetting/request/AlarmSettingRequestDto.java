package com.poppin.poppinserver.alarm.dto.alarmSetting.request;

import lombok.Builder;

@Builder
public record AlarmSettingRequestDto(

        String fcmToken,
        String pushYn,
        String pushNightYn,
        String hoogiYn,
        String openYn,
        String magamYn,
        String changeInfoYn

) {

    public static AlarmSettingRequestDto fromEntity(String token, String pushYn, String pushNightYn, String hoogiYn, String openYn, String magamYn, String changeInfoYn) {
        return AlarmSettingRequestDto.builder()
                .fcmToken(token)
                .pushYn(pushYn)
                .pushNightYn(pushNightYn)
                .hoogiYn(hoogiYn)
                .openYn(openYn)
                .magamYn(magamYn)
                .changeInfoYn(changeInfoYn)
                .build();
    }

}
