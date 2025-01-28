package com.poppin.poppinserver.alarm.usecase;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.core.annotation.UseCase;

import java.util.List;

@UseCase
public interface AlarmQueryUseCase {

    List<InformAlarm> getInformAlarms(Long userId);
}
