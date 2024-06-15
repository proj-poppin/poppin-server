package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.Visit;


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

    @Query("SELECT COUNT(v) FROM Visit  v  WHERE v.createdAt >= :thirtyMinutesAgo AND v.user.id =  :userId AND v.popup.id = :popupId")
    Integer findDuplicateVisitors(@Param("userId") Long userId , @Param("popupId") Long popupId, @Param("thirtyMinutesAgo") LocalDateTime thirtyMinutesAgo );

    @Query("SELECT v FROM Visit v JOIN v.user u JOIN v.popup p WHERE v.user.id = :userId AND  v.popup.id = :popupId")
    Visit findByUserIdAndPopupId(@Param("userId") Long userId , @Param("popupId") Long popupId);

    @Query("SELECT v FROM Visit v WHERE v.user.id = :userId")
    List<Visit> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT v FROM Visit v WHERE v.user.id = :userId AND v.popup.id = :popupId")
    Optional<Visit> findByUserId(@Param("userId")Long userId, @Param("popupId") Long popupId);

    // createdAt 칼럼이 주어진 날짜 시간 이전인 모든 Visit을 찾습니다.
    List<Visit> findAllByCreatedAtBefore(LocalDateTime dateTime);

    // createdAt 칼럼이 주어진 날짜 시간 이전인 모든 Visit을 삭제합니다.
    void deleteAllByCreatedAtBefore(LocalDateTime dateTime);

    void deleteAllByPopupId(Long popupId);
}
