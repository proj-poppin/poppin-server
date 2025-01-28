package com.poppin.poppinserver.alarm.service;

import com.poppin.poppinserver.alarm.domain.Alarm;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.domain.UserInformAlarm;
import com.poppin.poppinserver.alarm.dto.alarm.request.NotificationRequestDto;
import com.poppin.poppinserver.alarm.dto.informAlarm.response.NoticeDto;
import com.poppin.poppinserver.alarm.repository.AlarmRepository;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.UserInformAlarmRepository;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.usecase.UserQueryUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final InformAlarmRepository informAlarmRepository;
    private final UserInformAlarmRepository userInformAlarmRepository;
    private final UserQueryUseCase userQueryUseCase;

    // 알림 읽음 처리
    @Transactional
    public String checkNotification(Long userId, NotificationRequestDto notificationRequestDto){

        Long alarmId;
        try {
            alarmId = Long.parseLong(notificationRequestDto.notificationId());
        } catch (NumberFormatException e) {
            throw new CommonException(ErrorCode.INVALID_PARAMETER);
        }

        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_ALARM));

        User user = userQueryUseCase.findUserById(userId);

        if (alarm instanceof InformAlarm) {

            InformAlarm informAlarm = (InformAlarm) alarm;

            UserInformAlarm userInformAlarm = userInformAlarmRepository.findByUserAndInformAlarm(user, informAlarm)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ALARM));

            if (!userInformAlarm.getIsRead()) {
                userInformAlarm.markAsRead();
                userInformAlarmRepository.save(userInformAlarm);
            }

        } else if (alarm instanceof PopupAlarm) {
            PopupAlarm popupAlarm = (PopupAlarm) alarm;

            log.info("PopupAlarm user: {}", popupAlarm.getUser());
            log.info("PopupAlarm isRead: {}", popupAlarm.getIsRead());

            if (popupAlarm.getUser() != null && popupAlarm.getUser().equals(user) && !popupAlarm.getIsRead()) {
                log.info("조건 만족 - 알림 읽음 처리");
                popupAlarm.markAsRead();
                alarmRepository.save(popupAlarm);
            } else {
                throw new CommonException(ErrorCode.ALARM_CHECK);
            }
        } else {
            throw new CommonException(ErrorCode.NOT_FOUND_ALARM_TYPE);
        }

        return "읽음 처리 완료";
    }

    public NoticeDto getNoticeById(Long userId, Long noticeId){

        User user = userQueryUseCase.findUserById(userId);

        InformAlarm informAlarm = informAlarmRepository.findById(noticeId)
                .orElseThrow(()->new CommonException(ErrorCode.NOT_FOUND_ALARM));

        UserInformAlarm userInformAlarm = userInformAlarmRepository.findByUserAndInformAlarm(user, informAlarm)
                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_INFO_ALARM));

        userInformAlarm.markAsRead();
        userInformAlarmRepository.save(userInformAlarm);

        return NoticeDto.fromEntity(informAlarm);
    }
}



