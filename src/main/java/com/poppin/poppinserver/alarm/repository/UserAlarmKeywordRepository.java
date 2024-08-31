package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserAlarmKeywordRepository extends JpaRepository<UserAlarmKeyword, Long> {
    @Query("SELECT uak FROM UserAlarmKeyword uak WHERE uak.user = :user")
    Set<UserAlarmKeyword> findAllByUser(User user);
}
