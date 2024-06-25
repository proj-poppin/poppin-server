package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.PopupAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupAlarmRepository extends JpaRepository<PopupAlarm, Long> {


    @Query("SELECT a FROM PopupAlarm a WHERE a.keyword = 'POPUP' AND a.isRead = false AND a.token = :token ORDER BY a.createdAt desc ")
    List<PopupAlarm> findByKeywordOrderByCreatedAtDesc(String token);

    @Query("SELECT a FROM PopupAlarm a WHERE a.popupId.id = :popupId")
    PopupAlarm findByPopupId(@Param("popupId") Long popupId);
}
