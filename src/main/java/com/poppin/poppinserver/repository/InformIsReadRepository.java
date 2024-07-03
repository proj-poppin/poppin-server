package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.InformIsRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformIsReadRepository extends JpaRepository<InformIsRead, Long> {

    @Query("SELECT inform FROM InformIsRead inform WHERE inform.user.id = :userId AND inform.informAlarm.id = :alarmId")
    InformIsRead findByUserIdAndInformId(@Param("userId") Long userId, @Param("alarmId") Long alarmId);

    @Query("SELECT a FROM InformIsRead a WHERE a.user.id = :userId AND a.isRead = false")
    List<InformIsRead> findUnreadInformAlarms(@Param("userId") Long userId);

}
