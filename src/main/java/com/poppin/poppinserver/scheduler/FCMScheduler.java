package com.poppin.poppinserver.scheduler;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.PopupTopicRepository;
import com.poppin.poppinserver.type.EPopupTopic;
import com.poppin.poppinserver.type.ETopicType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Configuration
@Slf4j
public class FCMScheduler {

    private final PopupRepository popupRepository;
    private final PopupTopicRepository popupTopicRepository;

    @Scheduled(cron = "0 */5 * * * *")
    public void fcmReopen(){

        /**
         * 1. popup 을 추출(조건 : 오픈일자가 현재보다 같거나 이후)
         * 2. popup topic 테이블에서 popup id + RO 조건으로
         */
        LocalDate now = LocalDate.now();
        Optional<List<Popup>> interestPopupId = popupRepository.findByInterestPopupIdReopen(now);
        if (interestPopupId.isEmpty())log.info("interest popup empty");
        else{
            List<String> tokenList = popupTopicRepository.findTokenIdByTopicAndType(EPopupTopic.REOPEN, ETopicType.RO);

        }
    }
}
