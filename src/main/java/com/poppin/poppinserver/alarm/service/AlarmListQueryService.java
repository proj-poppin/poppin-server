package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.repository.InformIsReadRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmListQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmListQueryService implements AlarmListQueryUseCase {

    private final InformIsReadRepository informIsReadRepository;
    private final PopupAlarmRepository popupAlarmRepository;


    @Override
    public int countUnreadAlarms(String fcmToken) {
        int resultCount;

        int unreadInformAlarms = informIsReadRepository.unreadInforms(fcmToken);
        int unreadPopupAlarms = popupAlarmRepository.UnreadPopupAlarms(fcmToken);

        resultCount = unreadInformAlarms + unreadPopupAlarms;

        return resultCount;
    }
}
