package com.poppin.poppinserver.core.scheduler;

import com.poppin.poppinserver.core.type.EOperationStatus;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.repository.PopupRepository;
import com.poppin.poppinserver.visit.domain.Visit;
import com.poppin.poppinserver.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class PopupScheduler {
    private final PopupRepository popupRepository;

    private final VisitRepository visitRepository;

    // 자정마다 팝업 상태 변경
    @Scheduled(cron = "0 0 0 * * *")
    public void changePopupOperatingStatus() {
        List<Popup> popups = popupRepository.findAllByOpStatusIsNotyetOrOperating();

        for (Popup popup : popups) {
            //현재 운영상태 수정
            if (popup.getOpenDate().isAfter(LocalDate.now())) { // 오픈 전
                popup.updateOpStatus(EOperationStatus.NOTYET.getStatus());
            } else if (popup.getCloseDate().isBefore(LocalDate.now())) { // 운영 종료
                popup.updateOpStatus(EOperationStatus.TERMINATED.getStatus());

                List<Visit> visits = visitRepository.findByPopupId(popup.getId());
                for (Visit visit : visits) visitRepository.delete(visit);
            } else { // 운영중
                popup.updateOpStatus(EOperationStatus.OPERATING.getStatus());
            }
        }

        popupRepository.saveAll(popups);
    }
}
