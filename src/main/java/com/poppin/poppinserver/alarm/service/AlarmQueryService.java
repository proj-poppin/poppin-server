package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmQueryUseCase;
import com.poppin.poppinserver.alarm.usecase.TokenQueryUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmQueryService implements AlarmQueryUseCase {

    private final InformAlarmRepository informAlarmRepository;
    private final TokenQueryUseCase tokenQueryUseCase;

    @Override
    public List<InformAlarm> getInformAlarms(Long userId) {
        FCMToken fcmToken = tokenQueryUseCase.findTokenByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_FCM_TOKEN));

        List<InformAlarm> informAlarms = informAlarmRepository.findByKeywordOrderByIdDesc(userId);
        return informAlarms;
    }
}
