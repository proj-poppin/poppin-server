package com.poppin.poppinserver.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.AlarmSetting;
import com.poppin.poppinserver.domain.NotificationToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
import com.poppin.poppinserver.repository.AlarmSettingRepository;
import com.poppin.poppinserver.repository.NotificationTokenRepository;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import com.poppin.poppinserver.type.EPushInfo;
import com.poppin.poppinserver.util.FCMSendUtil;
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
    private final NotificationTokenRepository notificationTokenRepository;
    private final AlarmSettingRepository alarmSettingRepository;
    private final FCMSendUtil fcmSendUtil;




    @Scheduled(cron = "0 */05 * * * *")
    public void reopenPopup(){

        /**
         * 재오픈 수요 팝업 재오픈 알림
         * 1. popup 을 추출(조건 : 오픈일자가 현재보다 같거나 이후)
         * 2. popup topic 테이블에서 popup id + RO 조건으로 token list 추출
         */
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        LocalDate now = zonedDateTime.toLocalDate();
        log.info("- - - - - - - - - - - - - - - - - - - - - 재오픈알림 배치 시작 - - - - - - - - - - - - - - - - - - - - -");

        List<Popup> reopenPopup = popupRepository.findReopenPopupWithDemand(now); // null, 1, many
        if (reopenPopup.isEmpty())log.info("사용자가 재오픈 수요 체크한 팝업 중 재오픈한 팝업이 없습니다."); // null 처리
        else{
            schedulerFcmPopupTopicByType(reopenPopup, EPopupTopic.REOPEN, EPushInfo.REOPEN);
        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void magamPopup(){
        /**
         * 마감 팝업 알림
         * 1. popup 을 추출(조건 : 마감 일자가 오늘~내일 사이 일때(24시간 이내) )
         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
         */
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        LocalDate now = zonedDateTime.toLocalDate();
        LocalDate tomorrow = zonedDateTime.toLocalDate().plusDays(1);

        log.info("- - - - - - - - - - - - - - - - - - - - - 마감팝업 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
        List<Popup> magamPopup = popupRepository.findMagamPopup(now, tomorrow); // null, 1, many
        if (magamPopup.isEmpty())log.info("사용자가 관심 팝업 등록한 팝업 중 마감 임박한 팝업이 없습니다."); // null 처리
        else{
            schedulerFcmPopupTopicByType(magamPopup, EPopupTopic.MAGAM, EPushInfo.MAGAM);
        }
    }

    @Scheduled(cron = "0 */05 * * * *")
    public void openPopup() {
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
        log.info("- - - - - - - - - - - - - - - - - - - - - 오픈팝업 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
        log.info("date : " + date);
        log.info("timeNow : " + timeNow);
        log.info("timeBefore : " + timeBefore);

          List<Popup> openPopup = popupRepository.findOpenPopup(date, timeNow, timeBefore);

        if (openPopup.isEmpty())log.info("관심 팝업 등록된 팝업 중 오픈된 팝업이 존재하지 않습니다.");
        else {
            schedulerFcmPopupTopicByType(openPopup,EPopupTopic.OPEN, EPushInfo.OPEN);
        }
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void hotPopup() {
        /**
         * 인기 팝업 알림
         * 1. popup 을 추출(조건 : 유저 생성 기점으로 7일 마다 인기 팝업 등록된 팝업 )
         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
         */
        log.info("- - - - - - - - - - - - - - - - - - - - - 인기팝업 배치 시작 - - - - - - - - - - - - - - - - - - - - -");

        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        // 7일 전 날짜 계산
        LocalDate now = zonedDateTime.toLocalDate();
        LocalDate weekAgo = now.minusWeeks(1);
        LocalDateTime startOfLastWeek = weekAgo.atStartOfDay();
        LocalDateTime endOfLastWeek = startOfLastWeek.plusDays(7);

        List<Popup> hotPopup = popupRepository.findTopOperatingPopupsByInterestAndViewCountAndUserCreate(startOfLastWeek, endOfLastWeek, PageRequest.of(0, 5));

        if (hotPopup.isEmpty())log.info("인기 팝업이 없습니다");
        else {
            fcmSendUtil.sendByFCMToken(hotPopup,EPushInfo.HOTPOPUP);
        }
    }

    /**
     * 후기 요청
     * 1. 팝업 방문하기 버튼 누르고 3시간이 지난 유저들에 한해 앱 푸시 알림 발송
     *
     */
//    @Scheduled(cron = "0 */05 * * * *")
//    public void hoogi() {
//        ZoneId zoneId = ZoneId.of("Asia/Seoul");
//        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
//
//        LocalDateTime now = zonedDateTime.toLocalDateTime();
//        LocalDateTime threeHoursAgo = now.minusHours(3);
//
//        log.info("- - - - - - - - - - - - - - - - - - - - - 후기요청 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
//        List<Popup> hoogiList = popupRepository.findHoogi(threeHoursAgo);
//        if (hoogiList.isEmpty())log.info("후기 요청을 보낼 팝업이 없습니다.");
//        else{
//            schedulerFcmPopupTopicByType(hoogiList,EPopupTopic.HOOGI, EPushInfo.HOOGI);
//        }
//    }


    /**
     * 스케줄러 FCM 앱 푸시 팝업 알림 공통 메서드
     * @param popupList : 팝업 리스트
     * @param topic : 팝업 주제
     * @param info : 푸시 알림 제목, 메시지
     * @throws FirebaseMessagingException
     */
    private void schedulerFcmPopupTopicByType(List<Popup> popupList,EPopupTopic topic, EPushInfo info) {

        List<FCMRequestDto> fcmRequestDtoList = new ArrayList<>();

        for (Popup popup : popupList){
            Long popupId = popup.getId();
            List<NotificationToken> tokenList = (notificationTokenRepository.findTokenIdByTopicAndType(topic.getCode(), popupId));
            if (tokenList.isEmpty()) log.info(topic + "에 대해 구독한 토큰이 없습니다.");
            else{
                for (NotificationToken token : tokenList){

                    // 알림 세팅을 "1"이라야 가능하게 함.
                    log.info("token : " + token.getToken());
                    AlarmSetting set = alarmSettingRepository.findByToken(token.getToken());
                    log.info("setting : " + set);
                    String setDefVal = set.getPushYn();
                    String setVal;
                    switch (topic){
                        case OPEN -> setVal = set.getOpenYn();
                        case MAGAM -> setVal = set.getMagamYn();
                        case CHANGE_INFO -> setVal = set.getChangeInfoYn();
                        case HOOGI -> setVal = set.getHoogiYn();
                        default -> setVal = "1";
                    }
                    if (setDefVal.equals("1") && setVal.equals("1")){
                        FCMRequestDto fcmRequestDto = new FCMRequestDto(popupId, token.getToken(), info.getTitle(), info.getBody() , topic);
                        fcmRequestDtoList.add(fcmRequestDto);
                    }
                }
            }
        }
        if (fcmRequestDtoList == null) {log.info(topic + "에 대해 메시지 발송할 토큰이 없습니다.");}
        else {fcmSendUtil.sendFCMTopicMessage(fcmRequestDtoList);} // 메시지 발송
    }
}
