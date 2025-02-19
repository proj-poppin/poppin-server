package com.poppin.poppinserver.alarm.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import com.poppin.poppinserver.alarm.domain.UserInformAlarm;
import com.poppin.poppinserver.alarm.dto.alarm.request.InformAlarmCreateRequestDto;
import com.poppin.poppinserver.alarm.dto.popupAlarm.request.PopupAlarmDto;
import com.poppin.poppinserver.alarm.repository.InformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.UserInformAlarmRepository;
import com.poppin.poppinserver.alarm.repository.PopupAlarmRepository;
import com.poppin.poppinserver.alarm.usecase.AlarmCommandUseCase;
import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmCommandService implements AlarmCommandUseCase {
    private final AmazonS3Client s3Client;

    private final PopupAlarmRepository popupAlarmRepository;
    private final InformAlarmRepository informAlarmRepository;
    private final UserInformAlarmRepository userInformAlarmRepository;

    @Value("${cloud.aws.s3.alarm.bucket.name}")
    private String alarmBucket;

    @Override
    public InformAlarm insertInformAlarm(InformAlarmCreateRequestDto requestDto, String url) {
        log.info("INFORM ALARM insert");

        try {
            String icon = s3Client.getUrl(alarmBucket, EPopupTopic.CHANGE_INFO.getImgName()).toString();

            InformAlarm alarm = InformAlarm.builder()
                    .title(requestDto.title())
                    .body(requestDto.body())
                    .icon(icon)
                    .build();

            return informAlarmRepository.save(alarm);

        } catch (CommonException e) {
            log.error("ERROR during saving alarm : " + e.getMessage());
            return null;
        }
    }

    @Override
    public String insertPopupAlarm(PopupAlarmDto popupAlarmDto) {
        log.info("POPUP ALARM insert");

        try {
            Popup popup     = popupAlarmDto.popup();
            User user       = popupAlarmDto.user();
            String title    = popupAlarmDto.title();
            String body     = popupAlarmDto.body();
            String icon = Objects.requireNonNull(getUrlForTopic(popupAlarmDto.topic())).toString();

            PopupAlarm alarm = PopupAlarm.builder()
                    .popup(popup)
                    .user(user)
                    .title(title)
                    .body(body)
                    .icon(icon)
                    .build();

            popupAlarmRepository.save(alarm);

            return "1";
        } catch (CommonException e) {
            log.error("ERROR during saving alarm : " + e.getMessage());
            return "0";
        }
    }

    @Override
    public void insertUserInform(User user, InformAlarm informAlarm) {
        UserInformAlarm userInformAlarm = new UserInformAlarm(user, informAlarm);
        userInformAlarmRepository.save(userInformAlarm);
    }

    @Override
    public URL getUrlForTopic(EPopupTopic topic) {
        URL url = s3Client.getUrl(alarmBucket, topic.getImgName());
        log.info("Generated URL for topic {}: {}", topic, url);
        return url;
    }
}
