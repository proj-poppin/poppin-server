package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.AlarmSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {
}
