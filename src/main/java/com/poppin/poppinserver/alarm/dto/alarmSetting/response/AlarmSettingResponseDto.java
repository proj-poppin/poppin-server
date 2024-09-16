package com.poppin.poppinserver.alarm.dto.alarmSetting.response;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import lombok.Builder;

@Builder
public record AlarmSettingResponseDto(

        String token,
        Boolean appPush,
        Boolean nightPush,
        Boolean helpfulReviewPush,
        Boolean interestedPopupOpenPush,
        Boolean interestedPopupDeadlinePush,
        Boolean interestedPopupInfoUpdatedPush

) {
    public static AlarmSettingResponseDto fromEntity(AlarmSetting alarmSetting) {
        return AlarmSettingResponseDto.builder()
                .token(alarmSetting.getToken())
                .appPush(alarmSetting.getPushYn())
                .nightPush(alarmSetting.getPushNightYn())
                .helpfulReviewPush(alarmSetting.getHoogiYn())
                .interestedPopupOpenPush(alarmSetting.getOpenYn())
                .interestedPopupDeadlinePush(alarmSetting.getMagamYn())
                .interestedPopupInfoUpdatedPush(alarmSetting.getChangeInfoYn())
                .build();
    }
}
