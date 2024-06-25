package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.InformAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InformAlarmRepository extends JpaRepository<InformAlarm, Long> {

    @Query(value = "SELECT * FROM inform_alarm ORDER BY ID DESC LIMIT 1" , nativeQuery = true)
    InformAlarm findInformAlarmOrderByIdDesc();

    @Query("SELECT a FROM InformAlarm a WHERE a.keyword = 'INFORM' AND a.isRead = false ORDER BY a.createdAt desc")
    List<InformAlarm> findByKeywordOrderByCreatedAtDesc();

    Optional<InformAlarm> findById(Long id);
}
