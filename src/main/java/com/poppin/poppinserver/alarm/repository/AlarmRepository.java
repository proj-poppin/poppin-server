package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

}
