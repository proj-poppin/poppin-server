package com.poppin.poppinserver.core.scheduler;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.core.type.EOperationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class PopupScheduler {
    private final PopupRepository popupRepository;

    // 자정마다 팝업 상태 변경
    @Scheduled(cron = "0 0 0 * * *")
    public void changePopupOperatingStatus() {
        List<Popup> popups = popupRepository.findAllByOpStatusIsNotyetOrOperating();

        for (Popup popup : popups) {
            //현재 운영상태 수정
            if (popup.getOpenDate().isAfter(LocalDate.now())) {
                popup.updateOpStatus(EOperationStatus.NOTYET.getStatus());
            } else if (popup.getCloseDate().isBefore(LocalDate.now())) {
                popup.updateOpStatus(EOperationStatus.TERMINATED.getStatus());
            } else {
                popup.updateOpStatus(EOperationStatus.OPERATING.getStatus());
            }
        }

        popupRepository.saveAll(popups);
    }
}
