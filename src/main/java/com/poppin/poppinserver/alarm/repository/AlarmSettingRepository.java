package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {

  @Query("SELECT a FROM AlarmSetting a WHERE a.token = :token")
  AlarmSetting findByToken(String token);
}
