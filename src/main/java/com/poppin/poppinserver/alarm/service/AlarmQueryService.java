package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmQueryUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmQueryService implements AlarmQueryUseCase {
    private final InformAlarmRepository informAlarmRepository;

    @Override
    public List<InformAlarm> getInformAlarms(Long userId) {
        return informAlarmRepository.findByKeywordOrderByIdDesc(userId);
    }
}
