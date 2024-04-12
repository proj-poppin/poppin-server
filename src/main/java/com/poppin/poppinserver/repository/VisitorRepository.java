package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.Visitor;

import com.poppin.poppinserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    @Query("SELECT COUNT(v) FROM Visitor v JOIN v.popup p WHERE v.createdAt >= :thirtyMinutesAgo AND p = :popup")
    Optional<Integer> showRealTimeVisitors(@Param("popup") Popup popup, @Param("thirtyMinutesAgo") LocalDateTime thirtyMinutesAgo);

    @Query("SELECT COUNT(v) FROM Visitor v JOIN v.user u JOIN v.popup p WHERE v.createdAt >= :thirtyMinutesAgo AND u = :user AND p = :popup")
    Integer findDuplicateVisitors(@Param("user") User user , @Param("popup") Popup popup, @Param("thirtyMinutesAgo") LocalDateTime thirtyMinutesAgo );

    @Query("SELECT v FROM Visitor v JOIN v.user u JOIN v.popup p WHERE v.user.id = :userId AND  v.popup.id = :popupId")
    Visitor findByUserIdAndPopupId(@Param("userId") Long userId , @Param("popupId") Long popupId);
}
