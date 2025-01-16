package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InformAlarmRepository extends JpaRepository<InformAlarm, Long> {

    @Query(value = "SELECT a FROM InformAlarm a ORDER BY a.id desc ")
    InformAlarm findInformAlarmOrderByIdDesc();

    @Query("SELECT a FROM InformAlarm a JOIN UserInformAlarm isRead ON a.id = isRead.informAlarm.id WHERE isRead.user.id = :userId ORDER BY a.id desc")
    List<InformAlarm> findByKeywordOrderByIdDesc(@Param("userId") Long userId);

    Optional<InformAlarm> findById(Long id);


}
