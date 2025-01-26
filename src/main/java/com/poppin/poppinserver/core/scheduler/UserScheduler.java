package com.poppin.poppinserver.core.scheduler;

import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import com.poppin.poppinserver.user.service.UserHardDeleteService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserScheduler {
    private final UserQueryRepository userQueryRepository;
    private final UserHardDeleteService userHardDeleteService;

    // 자정마다 soft delete 한 지 30일이 지난 유저 삭제
    // @Scheduled(cron = "0 */3 * * * *") // test 3분마다
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void hardDeleteUser() {
        List<User> users = userQueryRepository.findAllByDeletedAtIsNotNullAndIsDeleted();

        List<User> usersToDelete = users.stream()
                .filter(user -> user.getDeletedAt().toLocalDate().isBefore(LocalDate.now().plusDays(1)))
                .collect(Collectors.toList());
        usersToDelete.forEach(userHardDeleteService::deleteAllRelatedInfo);
        userQueryRepository.deleteAllInBatch(usersToDelete);
        log.info("deleteAllInBatch 유저 최종 삭제 완료");
    }
}
