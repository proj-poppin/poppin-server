package com.poppin.poppinserver.review.repository;

import com.poppin.poppinserver.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageQueryRepository extends JpaRepository<ReviewImage, Long> {

    @Query("SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    List<String> findUrlAllByReviewId(@Param("reviewId") Long reviewId);

    List<ReviewImage> findAllByReviewId(Long reviewId);
}
