package com.poppin.poppinserver.dto.alarm.request;

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
