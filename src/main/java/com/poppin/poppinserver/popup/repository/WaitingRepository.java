package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.Waiting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long> {
    @Query("SELECT w FROM Waiting w WHERE w.user.id = :userId")
    List<Waiting> findAllByUserId(Long userId);
}
