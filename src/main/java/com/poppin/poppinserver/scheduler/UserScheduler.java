package com.poppin.poppinserver.scheduler;

import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Configuration
public class UserScheduler {
    private final UserRepository userRepository;

    // 자정마다 soft delete 한 지 30일이 지난 유저 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void hardDeleteUser(){
        List<User> users = userRepository.findAllByDeletedAtIsNotNull();

        List<User> usersToDelete = users.stream()
                .filter(user -> user.getDeletedAt().toLocalDate().isBefore(LocalDate.now().plusDays(1)))
                .collect(Collectors.toList());

        userRepository.deleteAllInBatch(usersToDelete);
    }
}
