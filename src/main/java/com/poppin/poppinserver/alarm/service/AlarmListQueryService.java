package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.repository.UserInformAlarmRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmListQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmListQueryService implements AlarmListQueryUseCase {

    private final UserInformAlarmRepository userInformAlarmRepository;
    private final PopupAlarmRepository popupAlarmRepository;


    @Override
    public int countUnreadAlarms(Long userId) {
        int resultCount;
        int unreadInformAlarms = userInformAlarmRepository.unreadInforms(userId);
        int unreadPopupAlarms = popupAlarmRepository.UnreadPopupAlarms(userId);

        resultCount = unreadInformAlarms + unreadPopupAlarms;

        return resultCount;
    }
}
