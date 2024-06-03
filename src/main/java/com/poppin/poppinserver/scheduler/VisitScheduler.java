package com.poppin.poppinserver.scheduler;

import com.poppin.poppinserver.repository.VisitRepository;
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
    public void deleteVisitData(){

        log.info("=====   방문일시가 1주일이 지난 데이터 삭제   =====");
        try {
            LocalDateTime oneWeekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
            visitRepository.deleteAllByCreatedAtBefore(oneWeekAgo);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

}
