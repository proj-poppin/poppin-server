package com.poppin.poppinserver.core.scheduler;

import com.poppin.poppinserver.visit.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class VisitScheduler {

    private final VisitRepository visitRepository;

    /**
     * 방문한지 1 주일이 지난 데이터 Visit 테이블에서 삭제
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteVisitData() {

        log.info("=====   방문일시가 1주일이 지난 데이터 삭제   =====");
        try {
            LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
            visitRepository.deleteAllByCreatedAtBefore(oneWeekAgo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

}
