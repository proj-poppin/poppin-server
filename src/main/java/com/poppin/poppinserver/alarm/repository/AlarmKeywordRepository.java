package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.AlarmKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmKeywordRepository extends JpaRepository<AlarmKeyword, Long> {

}
