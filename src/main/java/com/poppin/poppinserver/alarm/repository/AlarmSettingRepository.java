package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.AlarmSetting;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {
    @Query("SELECT a FROM AlarmSetting a WHERE a.user = :user")
    AlarmSetting findByUser(User user);
}
