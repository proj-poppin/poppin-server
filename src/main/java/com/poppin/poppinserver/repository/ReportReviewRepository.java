package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ReportReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportReviewRepository extends JpaRepository<ReportReview, Long> {
}
