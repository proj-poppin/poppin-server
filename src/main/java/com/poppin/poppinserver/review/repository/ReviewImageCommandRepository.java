package com.poppin.poppinserver.review.repository;

import com.poppin.poppinserver.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewImageCommandRepository extends JpaRepository<ReviewImage, Long> {
    @Modifying
    @Query("DELETE FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    void deleteAllByReviewId(Long reviewId);
}
