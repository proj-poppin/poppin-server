package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.Visit;

import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.domain.VisitorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    @Query("SELECT COUNT(v) FROM Visit v JOIN v.popup p WHERE v.createdAt >= :thirtyMinutesAgo AND p = :popup")
    Optional<Integer> showRealTimeVisitors(@Param("popup") Popup popup, @Param("thirtyMinutesAgo") LocalDateTime thirtyMinutesAgo);

    @Query("SELECT COUNT(v) FROM Visit  v JOIN v.user u JOIN v.popup p WHERE v.createdAt >= :thirtyMinutesAgo AND u = :user AND p = :popup")
    Integer findDuplicateVisitors(@Param("user") User user , @Param("popup") Popup popup, @Param("thirtyMinutesAgo") LocalDateTime thirtyMinutesAgo );

    @Query("SELECT v FROM Visit v JOIN v.user u JOIN v.popup p WHERE v.user.id = :userId AND  v.popup.id = :popupId")
    Visit findByUserIdAndPopupId(@Param("userId") Long userId , @Param("popupId") Long popupId);

    @Query("SELECT v FROM Visit v WHERE v.user.id = :userId")
    List<Visit> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT v FROM Visit v WHERE v.user.id = :userId AND v.popup.id = :popupId")
    Visit findByUserId(@Param("userId")Long userId, @Param("popupId") Long popupId);
}
