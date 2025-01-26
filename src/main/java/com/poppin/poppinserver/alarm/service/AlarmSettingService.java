package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSettingService {

    private final AlarmSettingRepository alarmSettingRepository;


}
