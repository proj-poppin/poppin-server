package com.poppin.poppinserver.review.repository;

import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewRecommend;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRecommendQueryRepository extends JpaRepository<ReviewRecommend, Long> {
    Optional<ReviewRecommend> findByUserAndReview(User user, Review review);

    @Query("SELECT r.review.id FROM ReviewRecommend r WHERE r.user.id = :userId")
    List<Long> recommendReviewList(@Param("userId") Long userId);
}
