package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAlarmKeywordRepository extends JpaRepository<UserAlarmKeyword, Long> {
    Optional<UserAlarmKeyword> findByUserId(User userId);
}
