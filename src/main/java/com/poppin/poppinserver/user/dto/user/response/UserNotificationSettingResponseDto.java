package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserNotificationSettingResponseDto(
        Boolean appPush,    // 앱 푸시 알림 수신 자체에 대한 설정
        Boolean nightPush,  // 야간 푸쉬알림
        Boolean helpfulReviewPush,  // 도움이 된 후기 리뷰 알림
        Boolean interestedPopupOpenPush,    // 관심 팝업 오픈 알림
        Boolean interestedPopupDeadlinePush,    // 관심 팝업 마감 D-1 알림
        Boolean interestedPopupInfoUpdatedPush,  // 관심 팝업 정보 변경 알림
        String lastCheck,   // 마지막으로 알람을 확인한 시간
        String lastUpdatedAt    // 마지막으로 알람 설정을 변경한 시간
) {
    public static UserNotificationSettingResponseDto fromEntity(AlarmSetting alarmSetting) {
        return UserNotificationSettingResponseDto.builder()
                .appPush(alarmSetting.getPushYn())
                .nightPush(alarmSetting.getPushNightYn())
                .helpfulReviewPush(alarmSetting.getHoogiYn())
                .interestedPopupOpenPush(alarmSetting.getOpenYn())
                .interestedPopupDeadlinePush(alarmSetting.getMagamYn())
                .interestedPopupInfoUpdatedPush(alarmSetting.getChangeInfoYn())
                .lastCheck(alarmSetting.getLastCheckedAt() == null ? "" : alarmSetting.getLastCheckedAt())
                .lastUpdatedAt(alarmSetting.getLastUpdatedAt() == null ? "" : alarmSetting.getLastUpdatedAt())
                .build();
    }
}
