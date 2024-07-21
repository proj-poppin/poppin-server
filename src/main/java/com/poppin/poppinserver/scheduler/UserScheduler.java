package com.poppin.poppinserver.scheduler;

import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.repository.UserRepository;
import com.poppin.poppinserver.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserScheduler {
    private final UserRepository userRepository;
    private final UserService userService;

    // 자정마다 soft delete 한 지 30일이 지난 유저 삭제
    // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void hardDeleteUser(){
        List<User> users = userRepository.findAllByDeletedAtIsNotNullAndIsDeleted();

        List<User> usersToDelete = users.stream()
                .filter(user -> user.getDeletedAt().toLocalDate().isBefore(LocalDate.now().plusDays(1)))
                .collect(Collectors.toList());
        log.info("usersToDelete 진입");
        usersToDelete.forEach(user -> userService.deleteAllRelatedInfo(user));
        log.info("deleteAllInBatch 유저 최종 삭제 진입");
        userRepository.deleteAllInBatch(usersToDelete);
        log.info("deleteAllInBatch 유저 최종 삭제 완료");
    }
}
