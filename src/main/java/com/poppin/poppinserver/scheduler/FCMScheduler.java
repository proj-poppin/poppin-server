package com.poppin.poppinserver.scheduler;

import com.poppin.poppinserver.repository.PopupTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;


@RequiredArgsConstructor
@Configuration
public class FCMScheduler {

    private final PopupTopicRepository popupTopicRepository;

    @Scheduled(cron = "0 */5 * * * *")
    public void fcmReopen(){

        LocalDate now = LocalDate.now();
        //Optional<List<Popup>> interestList = popupTopicRepository.findByInterestPopupIdReopen(now);

    }
}
