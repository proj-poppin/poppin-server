package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.dto.user.response.UserNotificationSettingResponseDto;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAlarmSettingService {
    private final AlarmSettingRepository alarmSettingRepository;
    private final UserQueryUseCase userQueryUseCase;

    public AlarmSetting getUserAlarmSetting(User user) {
        return Optional.ofNullable(alarmSettingRepository.findByUser(user))
                .orElseGet(() -> createUserAlarmSetting(user));
    }

    private AlarmSetting createUserAlarmSetting(User user) {
        return alarmSettingRepository.save(
                AlarmSetting.createAlarmSetting(user)
        );
    }

    @Transactional
    public UserNotificationSettingResponseDto updateAlarmSetting(Long userId, AlarmSettingRequestDto reqDto) {

        User user = userQueryUseCase.findUserById(userId);

        AlarmSetting alarmSetting = alarmSettingRepository.findByUser(user);

        if (alarmSetting == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_ALARM_SETTING);
        }

        alarmSetting.updateAlarmSetting(
                reqDto.lastCheck(),
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
