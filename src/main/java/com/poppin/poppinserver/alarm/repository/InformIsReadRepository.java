package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.InformIsRead;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InformIsReadRepository extends JpaRepository<InformIsRead, Long> {

    @Query("SELECT inform FROM InformIsRead inform " +
            "WHERE inform.fcmToken.token = :fcmToken AND inform.informAlarm.id = :alarmId")
    InformIsRead findByFcmTokenAndInformAlarm(@Param("fcmToken") String fcmToken, @Param("alarmId") Long alarmId);

    @Query("SELECT inform FROM InformIsRead inform WHERE inform.fcmToken.token = :fcmToken AND inform.isRead = false")
    List<InformIsRead> findUnreadInformAlarms(@Param("fcmToken") String fcmToken);

    @Query("SELECT COUNT(inform) from InformIsRead inform WHERE inform.fcmToken.token = :fcmToken AND inform.isRead = false")
    int unreadInforms(@Param("fcmToken") String fcmToken);

    @Query("SELECT informIsRead.informAlarm.id " +
            "FROM InformIsRead informIsRead " +
            "WHERE informIsRead.fcmToken.token = :fcmToken AND informIsRead.isRead = true")
    List<Long> findReadInformAlarmIdsByFcmToken(@Param("fcmToken") String fcmToken);

    @Query("SELECT MAX(informIsRead.readAt) " +
            "FROM InformIsRead informIsRead " +
            "WHERE informIsRead.fcmToken.token = :fcmToken AND informIsRead.isRead = true")
    String findLastReadTimeByFcmToken(@Param("fcmToken") String fcmToken);

    @Query("SELECT informIsRead FROM InformIsRead informIsRead WHERE informIsRead.fcmToken.token = :fcmToken")
    List<InformIsRead> findAllByFcmToken(@Param("fcmToken") String token);
}
