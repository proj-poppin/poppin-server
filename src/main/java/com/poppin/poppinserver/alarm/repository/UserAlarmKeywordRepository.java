package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAlarmKeywordRepository extends JpaRepository<UserAlarmKeyword, Long> {
    UserAlarmKeyword findByUserId(User userId);
}
