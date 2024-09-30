package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAlarmSettingService {
    private final AlarmSettingRepository alarmSettingRepository;

    public AlarmSetting getUserAlarmSetting(String fcmToken) {
        AlarmSetting alarmSetting = alarmSettingRepository.findByToken(fcmToken);
        if (alarmSetting == null) {
            alarmSetting = createUserAlarmSetting(fcmToken);
        }
        return alarmSetting;
    }

    private AlarmSetting createUserAlarmSetting(String fcmToken) {
        AlarmSetting alarmSetting = new AlarmSetting(fcmToken, true, true, true, true, true, true);
        return alarmSettingRepository.save(alarmSetting);
    }
}
