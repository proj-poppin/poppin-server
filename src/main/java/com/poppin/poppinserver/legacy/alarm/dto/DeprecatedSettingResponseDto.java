//package com.poppin.poppinserver.legacy.alarm.dto;
//
//import com.poppin.poppinserver.alarm.domain.AlarmSetting;
//import lombok.Builder;
//
//// TODO: 삭제 예정
//@Builder
//public record SettingResponseDto(
//        String fcmToken,
//        Boolean pushYn,
//        Boolean pushNightYn,
//        Boolean hoogiYn,
//        Boolean openYn,
//        Boolean magamYn,
//        Boolean changeInfoYn
//) {
//
//    public static SettingResponseDto fromEntity(AlarmSetting setting) {
//        return SettingResponseDto.builder()
//                .fcmToken(setting.getToken())
//                .pushYn(setting.getPushYn())
//                .pushNightYn(setting.getPushNightYn())
//                .hoogiYn(setting.getHoogiYn())
//                .openYn(setting.getOpenYn())
//                .magamYn(setting.getMagamYn())
//                .changeInfoYn(setting.getChangeInfoYn())
//                .build();
//    }
//}
