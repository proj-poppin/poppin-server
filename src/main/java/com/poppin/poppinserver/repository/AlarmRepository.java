package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {


    @Query("SELECT a FROM Alarm a WHERE a.keyword = 'POPUP' AND a.token = :token ORDER BY a.createdAt desc ")
    List<Alarm> findByKeywordOrderByCreatedAtDesc(String token);
}
