package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.PopupAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupAlarmRepository extends JpaRepository<PopupAlarm, Long> {


    @Query("SELECT a FROM PopupAlarm a WHERE a.keyword = 'POPUP' AND a.token = :token ORDER BY a.createdAt desc ")
    List<PopupAlarm> findByKeywordOrderByCreatedAtDesc(String token);
}
