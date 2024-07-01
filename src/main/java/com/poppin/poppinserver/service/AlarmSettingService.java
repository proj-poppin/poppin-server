package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.AlarmSetting;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.alarm.request.FcmTokenAlarmRequestDto;
import com.poppin.poppinserver.dto.alarm.response.SettingResponseDto;
import com.poppin.poppinserver.dto.alarmSetting.request.AlarmSettingRequestDto;
import com.poppin.poppinserver.dto.alarmSetting.response.AlarmSettingResponseDto;

import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.AlarmSettingRepository;
import com.poppin.poppinserver.repository.UserRepository;
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
                .orElseThrow(()->new CommonException(ErrorCode.NOT_FOUND_USER));

        AlarmSetting alarmSetting = alarmSettingRepository.findByToken(reqDto.fcmToken());
        if (alarmSetting == null) throw new CommonException(ErrorCode.NOT_FOUND_ALARM_SETTING);

        alarmSettingRepository.delete(alarmSetting); // 삭제

        AlarmSetting newAlarmSetting = new AlarmSetting(
                reqDto.fcmToken(),
                reqDto.pushYn(),
                reqDto.pushNightYn(),
                reqDto.hoogiYn(),
                reqDto.openYn(),
                reqDto.magamYn(),
                reqDto.changeInfoYn()
        );
        alarmSettingRepository.save(newAlarmSetting);
        AlarmSettingResponseDto resDto = AlarmSettingResponseDto.fromEntity(
                reqDto.fcmToken(),
                reqDto.pushYn(),
                reqDto.pushNightYn(),
                reqDto.hoogiYn(),
                reqDto.openYn(),
                reqDto.magamYn(),
                reqDto.changeInfoYn()
        );
        return resDto;
    }


    public SettingResponseDto readAlarmSetting(Long userId, FcmTokenAlarmRequestDto reqDto){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CommonException(ErrorCode.NOT_FOUND_USER));

        AlarmSetting setting = alarmSettingRepository.findByToken(reqDto.fcmToken());

        SettingResponseDto resDto = SettingResponseDto.fromEntity(setting);

        return resDto;
    }

}
