package com.poppin.poppinserver.dto.alarmSetting.response;

import lombok.Builder;

@Builder
public record AlarmSettingResponseDto(

        String token,
        String pushYn,
        String pushNightYn,
        String hoogiYn,
        String openYn,
        String magamYn,
        String changeInfoYn

) {
    public static AlarmSettingResponseDto fromEntity(String token, String pushYn, String pushNightYn, String hoogiYn, String openYn, String magamYn, String changeInfoYn) {
        return AlarmSettingResponseDto.builder()
                .token(token)
                .pushYn(pushYn)
                .pushNightYn(pushNightYn)
                .hoogiYn(hoogiYn)
                .openYn(openYn)
                .magamYn(magamYn)
                .changeInfoYn(changeInfoYn)
                .build();
    }
}
