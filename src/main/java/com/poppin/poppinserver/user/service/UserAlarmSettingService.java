package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAlarmSettingService {
    private final AlarmSettingRepository alarmSettingRepository;

    public AlarmSetting getUserAlarmSetting(String fcmToken) {
        return Optional.ofNullable(alarmSettingRepository.findByToken(fcmToken))
                .orElseGet(() -> createUserAlarmSetting(fcmToken));
    }

    private AlarmSetting createUserAlarmSetting(String fcmToken) {
        return alarmSettingRepository.save(
                AlarmSetting.createAlarmSetting(fcmToken)
        );
    }
}
