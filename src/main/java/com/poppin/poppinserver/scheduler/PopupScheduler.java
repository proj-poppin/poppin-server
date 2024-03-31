package com.poppin.poppinserver.scheduler;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class PopupScheduler {
    private final PopupRepository popupRepository;

    // 자정마다 팝업 상태 변경
    @Scheduled(cron = "0 0 0 * * *")
    public void changePopupOperatingStatus(){
        List<Popup> popups = popupRepository.findAllByOpStatusNotTerminated();

        for(Popup popup : popups){
            //현재 운영상태 수정
            if (popup.getOpenDate().isAfter(LocalDate.now())){
                Period period = Period.between(LocalDate.now(), popup.getOpenDate());
                popup.updateOpStatus("D-" + period.getDays());
            } else if (popup.getCloseDate().isBefore(LocalDate.now())) {
                popup.updateOpStatus("TERMINATED");
            }
        }

        popupRepository.saveAll(popups);
    }
}
