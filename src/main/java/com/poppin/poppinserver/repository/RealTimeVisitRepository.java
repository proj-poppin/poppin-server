package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.RealTimeVisit;

import com.poppin.poppinserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RealTimeVisitRepository extends JpaRepository<RealTimeVisit, Long> {
    @Query("SELECT COUNT(rtv) FROM RealTimeVisit rtv JOIN rtv.popup p WHERE rtv.createdAt >= :thirtyMinutesAgo AND p = :popup")
    Optional<Integer> showRealTimeVisitors(@Param("popup") Popup popup, @Param("thirtyMinutesAgo") LocalDateTime thirtyMinutesAgo);

    @Query("SELECT COUNT(rtv) FROM RealTimeVisit rtv JOIN rtv.user u JOIN rtv.popup p WHERE rtv.createdAt >= :thirtyMinutesAgo AND u = :user AND p = :popup")
    Integer findDuplicateVisitors(@Param("user") User user , @Param("popup") Popup popup, @Param("thirtyMinutesAgo") LocalDateTime thirtyMinutesAgo );
}
