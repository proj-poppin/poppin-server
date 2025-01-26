package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationSettingResponseDto;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSettingService {

    private final AlarmSettingRepository alarmSettingRepository;
    private final UserQueryUseCase userQueryUseCase;

    @Transactional
    public UserNotificationSettingResponseDto updateAlarmSetting(Long userId, AlarmSettingRequestDto reqDto) {

        User user = userQueryUseCase.findUserById(userId);

        AlarmSetting alarmSetting = alarmSettingRepository.findByUser(user);

        if (alarmSetting == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_ALARM_SETTING);
        }

        alarmSetting.updateAlarmSetting(
                reqDto.appPush(),
                reqDto.nightPush(),
                reqDto.helpfulReviewPush(),
                reqDto.interestedPopupOpenPush(),
                reqDto.interestedPopupDeadlinePush(),
                reqDto.interestedPopupInfoUpdatedPush()
        );

        return UserNotificationSettingResponseDto.fromEntity(alarmSetting);
    }
}
