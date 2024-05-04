package com.poppin.poppinserver.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.dto.notification.request.FCMRequestDto;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.PopupTopicRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import com.poppin.poppinserver.type.EPushInfo;
import com.poppin.poppinserver.type.ETopicType;
import com.poppin.poppinserver.util.NotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;


@RequiredArgsConstructor
@Configuration
@Slf4j
public class FCMScheduler {

    private final PopupRepository popupRepository;
    private final PopupTopicRepository popupTopicRepository;
    private final NotificationUtil notificationUtil;

    @Scheduled(cron = "0 */5 * * * *")
    public void fcmReopen() throws FirebaseMessagingException {

        /**
         * 재오픈 수요 팝업 재오픈 알림
         * 1. popup 을 추출(조건 : 오픈일자가 현재보다 같거나 이후)
         * 2. popup topic 테이블에서 popup id + RO 조건으로 token list 추출
         */
        LocalDate now = LocalDate.now();
        List<Popup> reopenPopup = popupRepository.findReopenPopup(now); // null, 1, many
        if (reopenPopup.isEmpty())log.info("사용자가 재오픈 수요 체크한 팝업 중 재오픈한 팝업이 없습니다."); // null 처리
        else{
            sendFcmPopupTopicByType(reopenPopup, EPopupTopic.REOPEN, ETopicType.RO, EPushInfo.REOPEN);
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void magamPopup() throws FirebaseMessagingException {
        /**
         *
         * 1. popup 을 추출(조건 : 마감 일자가 )
         * 2. popup topic 테이블에서 popup id + IP 조건으로 token list 추출
         */
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Popup> magamPopup = popupRepository.findMagamPopup(now, tomorrow); // null, 1, many
        if (magamPopup.isEmpty())log.info("사용자가 관심 팝업 등록한 팝업 중 마감 임박한 팝업이 없습니다."); // null 처리
        else{
            sendFcmPopupTopicByType(magamPopup, EPopupTopic.MAGAM, ETopicType.IP, EPushInfo.MAGAM);
        }
    }


    /**
     *
     * @param popupList
     * @param topic
     * @param type
     * @param info
     * @throws FirebaseMessagingException
     */
    private void sendFcmPopupTopicByType(List<Popup> popupList,EPopupTopic topic, ETopicType type, EPushInfo info) throws FirebaseMessagingException {

        List<FCMRequestDto> fcmRequestDtoList = null;
        for (Popup popup : popupList){
            Long popupId = popup.getId();
            List<String> tokenList = (popupTopicRepository.findTokenIdByTopicAndType(topic, type, popupId));
            if (tokenList.isEmpty()) log.info(topic.getTopicName() + "에 대해 구독한 토큰이 없습니다.");
            else{
                for (String token : tokenList){
                    FCMRequestDto fcmRequestDto = new FCMRequestDto(popupId, token, info.getTitle(), info.getBody());
                    fcmRequestDtoList.add(fcmRequestDto);
                }
            }
        }
        if (fcmRequestDtoList == null) {log.info(topic.getTopicName() + "에 대해 메시지 발송할 토큰이 없습니다.");}
        else {notificationUtil.sendFCMTopicMessage(fcmRequestDtoList);} // 메시지 발송
    }
}
