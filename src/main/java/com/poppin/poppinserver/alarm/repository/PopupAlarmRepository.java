package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.alarm.domain.PopupAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PopupAlarmRepository extends JpaRepository<PopupAlarm, Long> {

    @Query("SELECT popup FROM PopupAlarm popup WHERE popup.token = :token ORDER BY popup.id DESC ")
    List<PopupAlarm> findByKeywordOrderByIdDesc(String token);

    @Query("SELECT popup FROM PopupAlarm popup WHERE popup.isRead = false AND popup.token = :fcmToken ORDER BY popup.createdAt desc ")
    List<PopupAlarm> findUnreadPopupAlarms(@Param("fcmToken") String fcmToken);

    @Query("SELECT popup FROM PopupAlarm popup WHERE popup.id = :alarmId")
    Optional<PopupAlarm> findById(@Param("alarmId") Long alarmId);

    @Query("SELECT COUNT(popup) FROM PopupAlarm popup WHERE popup.isRead = false AND popup.token = :fcmToken")
    int UnreadPopupAlarms(@Param("fcmToken") String fcmToken);

    void deleteAllByPopupId(Popup popup);
}
