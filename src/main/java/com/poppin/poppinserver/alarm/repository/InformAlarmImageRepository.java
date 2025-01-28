package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.InformAlarmImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformAlarmImageRepository extends JpaRepository<InformAlarmImage, Long> {

    @Query(value = "SELECT * FROM inform_alarm_image ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<InformAlarmImage> findAlarmImageOrderByIdDesc();


    @Query("SELECT img FROM InformAlarmImage img WHERE img.informAlarm.id = :id")
    Optional<InformAlarmImage> findByAlarmId(@Param("id") Long id);
}
