package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import com.poppin.poppinserver.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAlarmSettingService {
    private final AlarmSettingRepository alarmSettingRepository;

    public AlarmSetting getUserAlarmSetting(User user) {
        return Optional.ofNullable(alarmSettingRepository.findByUser(user))
                .orElseGet(() -> createUserAlarmSetting(user));
    }

    private AlarmSetting createUserAlarmSetting(User user) {
        return alarmSettingRepository.save(
                AlarmSetting.createAlarmSetting(user)
        );
    }
}
