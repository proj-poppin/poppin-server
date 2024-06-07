//package com.poppin.poppinserver.scheduler;
//
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.poppin.poppinserver.domain.Popup;
//import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
//import com.poppin.poppinserver.repository.PopupRepository;
//import com.poppin.poppinserver.repository.PopupTopicRepository;
//import com.poppin.poppinserver.type.EPopupTopic;
//import com.poppin.poppinserver.type.EPushInfo;
//import com.poppin.poppinserver.util.FCMSendUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//
//@RequiredArgsConstructor
//@Configuration
//@Slf4j
//public class FCMScheduler {
//
//    private final PopupRepository popupRepository;
//    private final PopupTopicRepository popupTopicRepository;
//    private final FCMSendUtil fcmSendUtil;
//
//    @Scheduled(cron = "0 */5 * * * *")
//    public void reopenPopup(){
//
//        /**
//         * 재오픈 수요 팝업 재오픈 알림
//         * 1. popup 을 추출(조건 : 오픈일자가 현재보다 같거나 이후)
//         * 2. popup topic 테이블에서 popup id + RO 조건으로 token list 추출
//         */
//        LocalDate now = LocalDate.now();
//        log.info("- - - - - - - - - - - - - - - - - - - - - 재오픈알림 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
//
//        List<Popup> reopenPopup = popupRepository.findReopenPopupWithDemand(now); // null, 1, many
//        if (reopenPopup.isEmpty())log.info("사용자가 재오픈 수요 체크한 팝업 중 재오픈한 팝업이 없습니다."); // null 처리
//        else{
//            schedulerFcmPopupTopicByType(reopenPopup, EPopupTopic.REOPEN, EPopupTopic.REOPEN.getTopicType(), EPushInfo.REOPEN);
//        }
//    }
//
//    @Scheduled(cron = "0 */5 * * * *")
//    public void magamPopup(){
//        /**
//         * 마감 팝업 알림
//         * 1. popup 을 추출(조건 : 마감 일자가 오늘~내일 사이 일때(24시간 이내) )
//         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
//         */
//        LocalDate now = LocalDate.now();
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//
//        log.info("- - - - - - - - - - - - - - - - - - - - - 마감팝업 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
//        List<Popup> magamPopup = popupRepository.findMagamPopup(now, tomorrow); // null, 1, many
//        if (magamPopup.isEmpty())log.info("사용자가 관심 팝업 등록한 팝업 중 마감 임박한 팝업이 없습니다."); // null 처리
//        else{
//            schedulerFcmPopupTopicByType(magamPopup, EPopupTopic.MAGAM, EPopupTopic.MAGAM.getTopicType(), EPushInfo.MAGAM);
//        }
//    }
//
//    @Scheduled(cron = "0 */5 * * * *")
//    public void openPopup() {
//        /**
//         * 오픈 팝업 알림
//         * 1. popup 을 추출(조건 : 오픈 시간이 현재 시간보다 같거나 클 때 )
//         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
//         */
//        LocalDate date = LocalDate.now();
//        String timeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//        String timeBefore = LocalTime.now().minusMinutes(5).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//
//        log.info("- - - - - - - - - - - - - - - - - - - - - 오픈팝업 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
//
//        List<Popup> openPopup = popupRepository.findOpenPopup(date, timeNow, timeBefore);
//
//        if (openPopup.isEmpty())log.info("관심 팝업 등록된 팝업 중 오픈된 팝업이 존재하지 않습니다.");
//        else {
//            schedulerFcmPopupTopicByType(openPopup,EPopupTopic.OPEN, EPopupTopic.OPEN.getTopicType(), EPushInfo.OPEN);
//        }
//    }
//
//    @Scheduled(cron = "0 0 0 * * MON")
//    public void hotPopup() {
//        /**
//         * 인기 팝업 알림
//         * 1. popup 을 추출(조건 : 유저 생성 기점으로 7일 마다 인기 팝업 등록된 팝업 )
//         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
//         */
//        log.info("- - - - - - - - - - - - - - - - - - - - - 인기팝업 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
//        // 7일 전 날짜 계산
//        LocalDate weekAgo = LocalDate.now().minusWeeks(1);
//        LocalDateTime startOfLastWeek = weekAgo.atStartOfDay();
//        LocalDateTime endOfLastWeek = startOfLastWeek.plusDays(7);
//
//        List<Popup> hotPopup = popupRepository.findTopOperatingPopupsByInterestAndViewCountAndUserCreate(startOfLastWeek, endOfLastWeek, PageRequest.of(0, 5));
//
//        if (hotPopup.isEmpty())log.info("인기 팝업이 없습니다");
//        else {
//            schedulerFcmPopupTopicByType(hotPopup,EPopupTopic.HOT, EPopupTopic.HOT.getTopicType(), EPushInfo.HOTPOPUP);
//        }
//    }
//
//    /**
//     * 후기 요청
//     * 1. 팝업 방문하기 버튼 누르고 3시간이 지난 유저들에 한해 앱 푸시 알림 발송
//     *
//     */
//    @Scheduled(cron = "0 */5 * * * *")
//    public void hoogi() {
//        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
//
//        log.info("- - - - - - - - - - - - - - - - - - - - - 후기요청 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
//        List<Popup> hoogiList = popupRepository.findHoogi(threeHoursAgo);
//        if (hoogiList.isEmpty())log.info("후기 요청을 보낼 팝업이 없습니다.");
//        else{
//            schedulerFcmPopupTopicByType(hoogiList,EPopupTopic.HOOGI, EPopupTopic.HOOGI.getTopicType(), EPushInfo.HOOGI);
//        }
//    }
//
//
//    @Scheduled(cron = "0 0 0 */2 * *")
//    public void jaebo() throws FirebaseMessagingException {
//        /**
//         * 제보 알림
//         *
//         */
//        log.info("- - - - - - - - - - - - - - - - - - - - - 제보 알림 배치 시작 - - - - - - - - - - - - - - - - - - - - -");
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//        LocalDateTime startOfDay = yesterday.atStartOfDay();
//        LocalDateTime endOfDay = yesterday.plusDays(1).atStartOfDay();
//
//        List<Popup> hotPopup = popupRepository.findTopOperatingPopupsByInterestAndViewCount(startOfDay, endOfDay, PageRequest.of(0, 5));
//
//        if (hotPopup.isEmpty())log.info("제보할 팝업이 없습니다");
//        else {
//            schedulerFcmPopupTopicByType(hotPopup,EPopupTopic.JAEBO, EPopupTopic.JAEBO.getTopicType(), EPushInfo.JAEBO);
//        }
//    }
//
//    /**
//     * 스케줄러 FCM 앱 푸시 팝업 알림 공통 메서드
//     * @param popupList : 팝업 리스트
//     * @param topic : 팝업 주제
//     * @param type : 팝업 주제 타입
//     * @param info : 푸시 알림 제목, 메시지
//     * @throws FirebaseMessagingException
//     */
//    private void schedulerFcmPopupTopicByType(List<Popup> popupList,EPopupTopic topic, String type, EPushInfo info) {
//
//        List<FCMRequestDto> fcmRequestDtoList = null;
//        for (Popup popup : popupList){
//            Long popupId = popup.getId();
//            List<String> tokenList = (popupTopicRepository.findTokenIdByTopicAndType(topic, type, popupId));
//            if (tokenList.isEmpty()) log.info(topic.getTopicName() + "에 대해 구독한 토큰이 없습니다.");
//            else{
//                for (String token : tokenList){
//                    FCMRequestDto fcmRequestDto = new FCMRequestDto(popupId, token, info.getTitle(), info.getBody() , topic);
//                    assert fcmRequestDtoList != null; // debug
//                    fcmRequestDtoList.add(fcmRequestDto);
//                }
//            }
//        }
//        if (fcmRequestDtoList == null) {log.info(topic.getTopicName() + "에 대해 메시지 발송할 토큰이 없습니다.");}
//        else {fcmSendUtil.sendFCMTopicMessage(fcmRequestDtoList);} // 메시지 발송
//    }
//}
