package com.poppin.poppinserver.core.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.alarm.dto.fcm.request.FCMRequestDto;
import com.poppin.poppinserver.alarm.repository.AlarmSettingRepository;
import com.poppin.poppinserver.alarm.repository.FCMTokenRepository;
import com.poppin.poppinserver.alarm.usecase.SendAlarmCommandUseCase;
import com.poppin.poppinserver.core.type.EPopupTopic;
import com.poppin.poppinserver.core.type.EPushInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.*;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Configuration
@Slf4j
public class FCMScheduler {

    private final PopupRepository popupRepository;
    private final FCMTokenRepository fcmTokenRepository;
    private final AlarmSettingRepository alarmSettingRepository;
    private final SendAlarmCommandUseCase sendAlarmCommandUseCase;

//    @Scheduled(cron = "0 0 */3 * * *")
//    private void reopenPopup() {
//
//        /**
//         * 재오픈 수요 팝업 재오픈 알림
//         * 1. popup 을 추출(조건 : 오픈일자가 현재보다 같거나 이후)
//         * 2.재오픈을 눌러놨던 유저의 토큰을 모두 가져와서 전송
//         */
//        ZoneId zoneId = ZoneId.of("Asia/Seoul");
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
//        LocalDate now = zonedDateTime.toLocalDate();
//        log.info("REOPEN popup scheduler start");
//
//        List<Popup> reopenPopup = popupRepository.findReopenPopupWithDemand(now); // null, 1, many
//        if (reopenPopup.isEmpty()) {
//            log.info("사용자가 재오픈 수요 체크한 팝업 중 재오픈한 팝업이 없습니다."); // null 처리
//        } else {
//            sendAlarmCommandUseCase.sendScheduledPopupAlarm(reopenPopup, EPushInfo.REOPEN);
//        }
//    }

    @Scheduled(cron = "0 0 9 * * *")
    private void magamPopup() {
        /**
         * 마감 팝업 알림
         * 1. popup 을 추출(조건 : 마감 일자가 오늘~내일 사이 일때(24시간 이내) )
         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
         */
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        LocalDate now = zonedDateTime.toLocalDate();
        LocalDate tomorrow = zonedDateTime.toLocalDate().plusDays(1);
        String topicCode = EPopupTopic.MAGAM.getCode();

        log.info("MAGAM popup scheduler start");
        List<Popup> magamPopup = popupRepository.findMagamPopup(now, tomorrow, topicCode); // null, 1, many
        if (magamPopup.isEmpty()) {
            log.info("사용자가 관심 팝업 등록한 팝업 중 마감 임박한 팝업이 없습니다."); // null 처리
        } else {
            schedulerFcmPopupTopicByType(magamPopup, EPopupTopic.MAGAM, EPushInfo.MAGAM);
        }
    }

    @Scheduled(cron = "0 0 */3 * * *")
    private void openPopup() {
        /**
         * 오픈 팝업 알림
         * 1. popup 을 추출(조건 : 오픈 시간이 현재 시간보다 같거나 클 때 )
         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
         */

        // 한국 시간 기준 현재 날짜와 시간
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        LocalDate date = zonedDateTime.toLocalDate();
        LocalTime timeNow = zonedDateTime.toLocalTime();
        LocalTime timeBefore = timeNow.minusMinutes(5);
        log.info("OPEN popup scheduler start");
        log.info("date : " + date);
        log.info("timeNow : " + timeNow);
        log.info("timeBefore : " + timeBefore);

        List<Popup> openPopup = popupRepository.findOpenPopup(date, timeNow, timeBefore);

        if (openPopup.isEmpty()) {
            log.info("관심 팝업 등록된 팝업 중 오픈된 팝업이 존재하지 않습니다.");
        } else {
            schedulerFcmPopupTopicByType(openPopup, EPopupTopic.OPEN, EPushInfo.OPEN);
        }
    }

    @Scheduled(cron = "0 0 13 * * MON")
    private void hotPopup() {
        /**
         * 인기 팝업 알림
         * 1. popup 을 추출(조건 : 유저 생성 기점으로 7일 마다 인기 팝업 등록된 팝업 )
         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
         */
        log.info("HOT popup scheduler start");

        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        // 7일 전 날짜 계산
        LocalDate now = zonedDateTime.toLocalDate();
        LocalDate weekAgo = now.minusWeeks(1);
        LocalDateTime startOfLastWeek = weekAgo.atStartOfDay();
        LocalDateTime endOfLastWeek = startOfLastWeek.plusDays(7);

        List<Popup> hotPopup = popupRepository.findHotPopup(startOfLastWeek, endOfLastWeek, PageRequest.of(0, 5));

        if (hotPopup.isEmpty()) {
            log.info("인기 팝업이 없습니다");
        } else {
            for (Popup p : hotPopup) {
                log.info("hot popup name {}", p.getName());
            }
            sendAlarmCommandUseCase.sendScheduledPopupAlarm(hotPopup, EPushInfo.HOTPOPUP);
        }
    }

    /**
     * 후기 요청 1. 팝업 방문하기 버튼 누르고 3시간이 지난 유저들에 한해 앱 푸시 알림 발송
     */
    @Scheduled(cron = "0 0 */3 * * *")
    private void hoogi() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        LocalDateTime now = zonedDateTime.toLocalDateTime();
        LocalDateTime threeHoursAndMin = now.minusHours(3).minusMinutes(5);
        LocalDateTime threeHoursAgo = now.minusHours(3);

        log.info("hoogi scheduler start");
        List<Popup> hoogiList = popupRepository.findHoogi(threeHoursAndMin, threeHoursAgo);
        if (hoogiList.isEmpty()) {
            log.info("후기 요청을 보낼 팝업이 없습니다.");
        } else {
            schedulerFcmPopupTopicByType(hoogiList, EPopupTopic.HOOGI, EPushInfo.HOOGI);
        }
    }


    /**
     * 스케줄러 FCM 앱 푸시 팝업 알림 공통 메서드
     *
     * @param popupList : 팝업 리스트
     * @param topic     : 팝업 주제
     * @param info      : 푸시 알림 제목, 메시지
     * @throws FirebaseMessagingException
     */
    public void schedulerFcmPopupTopicByType(List<Popup> popupList, EPopupTopic topic, EPushInfo info) {

        List<FCMRequestDto> fcmRequestDtoList = new ArrayList<>();

        for (Popup popup : popupList) {
            Long popupId = popup.getId();
            log.info("topic code : {}", topic.getCode());
            log.info("popupId : {}", popupId);
            List<FCMToken> tokenList = (fcmTokenRepository.findTokenIdByTopicAndType(topic.getCode(), popupId));
            if (tokenList.isEmpty()) {
                log.info("nothing subscribed on : " + topic);
            } else {
                for (FCMToken token : tokenList) {
                    User user = token.getUser();
                    // 알림 세팅을 "1"이라야 가능하게 함.
                    log.info("token : {}", token.getToken());
                    AlarmSetting set = alarmSettingRepository.findByUser(user);
                    if (set == null) {
                        log.warn("알림 설정이 존재하지 않는 사용자: {}", user.getId());
                        continue;
                    }
                    log.info("setting : {}", set);
                    Boolean setDefVal = set.getPushYn();
                    Boolean setVal;
                    switch (topic) {
                        case OPEN -> setVal = set.getOpenYn();
                        case MAGAM -> setVal = set.getMagamYn();
                        case CHANGE_INFO -> setVal = set.getChangeInfoYn();
                        case HOOGI -> setVal = set.getHoogiYn();
                        default -> setVal = true;
                    }

                    log.info("pushYn value : {}", setDefVal);
                    log.info("topic setting value : {}", setVal);

                    if (setDefVal.equals(true) && setVal.equals(true)) {
                        if (
                                info.equals(EPushInfo.HOTPOPUP) ||
                                        info.equals(EPushInfo.MAGAM) ||
                                        info.equals(EPushInfo.REOPEN) ||
                                        info.equals(EPushInfo.KEYWORD)
                        ) {
                            FCMRequestDto fcmRequestDto = new FCMRequestDto(
                                    String.valueOf(popupId),
                                    token.getToken(),
                                    info.getTitle(),
                                    "[" + popup.getName() + "] " + info.getBody(),
                                    topic
                            );
                            fcmRequestDtoList.add(fcmRequestDto);
                        } else if (
                                info.equals(EPushInfo.OPEN)
                        ) {
                            FCMRequestDto fcmRequestDto = new FCMRequestDto(String.valueOf(popupId), token.getToken(),
                                    "[" + popup.getName() + "] " + info.getTitle(), info.getBody(), topic);
                            fcmRequestDtoList.add(fcmRequestDto);
                        } else {
                            FCMRequestDto fcmRequestDto = new FCMRequestDto(String.valueOf(popupId), token.getToken(),
                                    info.getTitle(),
                                    info.getBody(), topic);
                            fcmRequestDtoList.add(fcmRequestDto);
                        }
                    }
                }
            }
        }
        if (fcmRequestDtoList == null) {
            log.info("tokens doesn't have existed on : " + topic);
        } else {
            sendAlarmCommandUseCase.sendPopupTopicAlarm(fcmRequestDtoList);
        }
    }
}
