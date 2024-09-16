package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.dto.alarm.request.AlarmTokenRequestDto;
import com.poppin.poppinserver.alarm.dto.alarm.response.SettingResponseDto;
import com.poppin.poppinserver.alarm.dto.alarmSetting.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.alarm.dto.alarmSetting.response.AlarmSettingResponseDto;
import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSettingService {

    private final AlarmSettingRepository alarmSettingRepository;
    private final UserRepository userRepository;

    public AlarmSettingResponseDto updateAlarmSetting(Long userId, AlarmSettingRequestDto reqDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        AlarmSetting alarmSetting = alarmSettingRepository.findByToken(reqDto.fcmToken());
        if (alarmSetting == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_ALARM_SETTING);
        }

        alarmSettingRepository.delete(alarmSetting); // 삭제

        AlarmSetting newAlarmSetting = new AlarmSetting(
                reqDto.fcmToken(),
                reqDto.appPush(),
                reqDto.nightPush(),
                reqDto.helpfulReviewPush(),
                reqDto.interestedPopupOpenPush(),
                reqDto.interestedPopupDeadlinePush(),
                reqDto.interestedPopupInfoUpdatedPush()
        );
        alarmSettingRepository.save(newAlarmSetting);

        return AlarmSettingResponseDto.fromEntity(newAlarmSetting);
    }


    public SettingResponseDto readAlarmSetting(Long userId, AlarmTokenRequestDto reqDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        AlarmSetting setting = alarmSettingRepository.findByToken(reqDto.fcmToken());

        SettingResponseDto resDto = SettingResponseDto.fromEntity(setting);

        return resDto;
    }

}
