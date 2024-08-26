package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.AlarmKeyword;
import com.poppin.poppinserver.alarm.domain.UserAlarmKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmKeywordRepository extends JpaRepository<AlarmKeyword, Long> {
    Optional<AlarmKeyword> findByUserAlarmKeywordAndKeyword(UserAlarmKeyword userAlarmKeyword, String keyword);
}
