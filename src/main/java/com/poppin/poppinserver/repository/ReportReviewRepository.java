package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ReportReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportReviewRepository extends JpaRepository<ReportReview, Long> {
    @Query("SELECT r FROM ReportReview r WHERE r.isExecuted = :isExec ORDER BY r.reportedAt DESC")
    Page<ReportReview> findAllByOrderByReportedAtDesc(Pageable pageable, @Param("isExec") Boolean isExec);

    @Modifying
    @Query("DELETE FROM ReportReview r WHERE r.reporterId.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ReportReview r WHERE r.reviewId.id = :reviewId")
    void deleteAllByReviewId(Long reviewId);
}
