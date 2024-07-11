package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.InformIsRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformIsReadRepository extends JpaRepository<InformIsRead, Long> {


    @Query("SELECT inform FROM InformIsRead inform " +
            "WHERE inform.fcmToken.token = :fcmToken AND inform.informAlarm.id = :alarmId")
    InformIsRead findByFcmTokenAndInformAlarm(@Param("fcmToken") String fcmToken, @Param("alarmId") Long alarmId);

    @Query("SELECT inform FROM InformIsRead inform WHERE inform.fcmToken.token = :fcmToken AND inform.isRead = false")
    List<InformIsRead> findUnreadInformAlarms(@Param("fcmToken") String fcmToken);

    @Query("SELECT COUNT(inform) from InformIsRead inform WHERE inform.fcmToken.token = :fcmToken AND inform.isRead = false")
    int unreadInforms(@Param("fcmToken") String fcmToken);

}
