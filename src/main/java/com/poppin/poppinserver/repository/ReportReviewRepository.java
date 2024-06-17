package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ReportReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportReviewRepository extends JpaRepository<ReportReview, Long> {
    @Query("SELECT r FROM ReportReview r ORDER BY r.reportedAt DESC")
    Page<ReportReview> findAllByOrderByReportedAtDesc(Pageable pageable);
}
