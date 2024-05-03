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
         * 1. popup 을 추출(조건 : 오픈일자가 현재보다 같거나 이후)
         * 2. popup topic 테이블에서 popup id + RO 조건으로 token list 추출
         */
        LocalDate now = LocalDate.now();
        List<Popup> interestPopupId = popupRepository.findByInterestPopupIdReopen(now); // null, 1, many
        if (interestPopupId.isEmpty())log.info("사용자가 관심 팝업 등록하고 재오픈한 팝업이 없습니다."); // null 처리
        else{
            List<FCMRequestDto> fcmRequestDtoList = null;
            for (Popup popup : interestPopupId){
                Long popupId = popup.getId();
                List<String> tokenList = (popupTopicRepository.findTokenIdByTopicAndType(EPopupTopic.REOPEN, ETopicType.RO, popupId));
                if (tokenList.isEmpty()) log.info("재오픈 하는 팝업을 구독한 토큰이 없습니다.");
                else{
                    for (String token : tokenList){
                        FCMRequestDto fcmRequestDto = new FCMRequestDto(popupId, token, EPushInfo.REOPEN.getTitle(), EPushInfo.REOPEN.getBody());
                        fcmRequestDtoList.add(fcmRequestDto);
                    }
                }
            }
            if (fcmRequestDtoList == null) return;
            notificationUtil.sendFCMTopicMessage(fcmRequestDtoList); // 메시지 발송
        }
    }
}
