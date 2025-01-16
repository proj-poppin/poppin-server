package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.InformAlarm;
import com.poppin.poppinserver.alarm.domain.UserInformAlarm;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserInformAlarmRepository extends JpaRepository<UserInformAlarm, Long> {


//    @Query("SELECT inform FROM UserInformAlarm inform WHERE inform.fcmToken.token = :fcmToken AND inform.isRead = false")
//    List<UserInformAlarm> findUnreadInformAlarms(@Param("fcmToken") String fcmToken);

    @Query("SELECT COUNT(userInformAlarm) from UserInformAlarm userInformAlarm WHERE userInformAlarm.user.id = :userId AND userInformAlarm.isRead = false")
    int unreadInforms(@Param("userId") Long userId);

    @Query("SELECT userInformAlarm.informAlarm.id " +
            "FROM UserInformAlarm userInformAlarm " +
            "WHERE userInformAlarm.user.id = :userId AND userInformAlarm.isRead = true")
    List<Long> findReadInformAlarmIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(userInformAlarm.readAt) " +
            "FROM UserInformAlarm userInformAlarm " +
            "WHERE userInformAlarm.user.id = :userId AND userInformAlarm.isRead = true")
    LocalDateTime findLastReadTimeByUser(@Param("userId") Long userId);

    @Query("SELECT userInformAlarm FROM UserInformAlarm userInformAlarm WHERE userInformAlarm.user.id = :userId")
    List<UserInformAlarm> findAllByUser(@Param("userId") Long userId);


    @Query("""
    SELECT UserInformAlarm 
    FROM UserInformAlarm UserInformAlarm
    WHERE UserInformAlarm.user = :user
      AND UserInformAlarm.informAlarm = :informAlarm
""")
    Optional<UserInformAlarm> findByUserAndInformAlarm(@Param("user") User user, @Param("informAlarm") InformAlarm informAlarm);

}
