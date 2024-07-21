package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.VisitorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface VisitorDataRepository extends JpaRepository<VisitorData, Long> {

    @Query("SELECT " +
            "CASE " +
            "   WHEN SUM(CASE WHEN v.congestion = 'CROWDED' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'NORMAL' THEN 1 ELSE 0 END) " +
            "       AND SUM(CASE WHEN v.congestion = 'CROWDED' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'RELAXED' THEN 1 ELSE 0 END) " +
            "   THEN '혼잡' " +
            "   WHEN SUM(CASE WHEN v.congestion = 'NORMAL' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'CROWDED' THEN 1 ELSE 0 END) " +
            "       AND SUM(CASE WHEN v.congestion = 'NORMAL' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'RELAXED' THEN 1 ELSE 0 END) " +
            "   THEN '보통' " +
            "   ELSE '여유' " +
            "END AS congestionRate, " +
            "CASE " +
            "   WHEN COUNT(v) = 0 THEN 0 " +
            "   ELSE " +
            "        SUM (CASE WHEN v.congestion = (SELECT CASE " +
            "                                           WHEN SUM(CASE WHEN v.congestion = 'CROWDED' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'NORMAL' THEN 1 ELSE 0 END) " +
            "                                               AND SUM(CASE WHEN v.congestion = 'CROWDED' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'RELAXED' THEN 1 ELSE 0 END) " +
            "                                           THEN 'CROWDED' " +
            "                                           WHEN SUM(CASE WHEN v.congestion = 'NORMAL' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'CROWDED' THEN 1 ELSE 0 END) " +
            "                                               AND SUM(CASE WHEN v.congestion = 'NORMAL' THEN 1 ELSE 0 END) > SUM(CASE WHEN v.congestion = 'RELAXED' THEN 1 ELSE 0 END) " +
            "                                           THEN 'NORMAL' " +
            "                                           ELSE 'RELAXED' " +
            "                                       END FROM VisitorData v WHERE p.id = :popupId AND v.visitDate = :visitDateEnum) THEN 1 ELSE 0 END) * 100 / (SUM(CASE WHEN v.congestion = 'CROWDED' THEN 1 ELSE 0 END) + " +
            "                                             SUM(CASE WHEN v.congestion = 'NORMAL' THEN 1 ELSE 0 END) + " +
            "                                             SUM(CASE WHEN v.congestion = 'RELAXED' THEN 1 ELSE 0 END)) " +
            "END AS congestionRatio " +
            "FROM VisitorData v JOIN Popup p ON p.id = v.popup.id " +
            "WHERE p.id = :popupId AND v.visitDate = :visitDateEnum " +
            "GROUP BY p.id")
    Map<String, Object> findCongestionRatioByPopupId(@Param("popupId") Long popupId, String visitDateEnum);

    @Query("SELECT COALESCE((SUM(CASE WHEN v.satisfaction = :satisfaction THEN 1 ELSE 0 END) * 100) / COUNT(r), 0) FROM VisitorData v JOIN Review r ON v.review.id = r.id JOIN r.popup p WHERE p.id = :popupId")
    Optional<Integer> satisfactionRate(@Param("popupId") Long popupId, @Param("satisfaction") String satisfaction);

    @Query("SELECT vd FROM VisitorData vd WHERE vd.review.id = :reviewId AND vd.popup.id = :popupId")
    VisitorData findByReviewIdAndPopupId(@Param("reviewId") Long reviewId, @Param("popupId") Long popupId);

    @Modifying
    @Query("DELETE FROM VisitorData vd WHERE vd.review.id = :reviewId")
    void deleteAllByReviewId(@Param("reviewId") Long reviewId);
}
