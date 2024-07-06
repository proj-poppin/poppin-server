package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.PopupAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupAlarmRepository extends JpaRepository<PopupAlarm, Long> {


    @Query("SELECT popup FROM PopupAlarm popup WHERE popup.keyword = 'POPUP' AND popup.token = :token ORDER BY popup.createdAt desc ")
    List<PopupAlarm> findByKeywordOrderByCreatedAtDesc(String token);

    @Query("SELECT popup FROM PopupAlarm popup WHERE popup.keyword = 'POPUP' AND popup.isRead = false AND popup.token = :fcmToken ORDER BY popup.createdAt desc ")
    List<PopupAlarm> findUnreadPopupAlarms(@Param("fcmToken") String fcmToken);

    @Query("SELECT popup FROM PopupAlarm popup WHERE popup.popupId.id = :popupId")
    PopupAlarm findByPopupId(@Param("popupId") Long popupId);

    @Query("SELECT COUNT(popup) FROM PopupAlarm popup WHERE popup.keyword = 'POPUP' AND popup.isRead = false AND popup.token = :fcmToken")
    int UnreadPopupAlarms(@Param("fcmToken") String fcmToken);
}
