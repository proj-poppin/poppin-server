package com.poppin.poppinserver.review.repository;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.review.domain.Review;
import com.poppin.poppinserver.review.domain.ReviewRecommend;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRecommendRepository extends JpaRepository<ReviewRecommend, Long> {

    Optional<ReviewRecommend> findByUserAndReview(User user, Review review);

    void deleteAllByReviewPopup(Popup popup);

    @Modifying
    @Query("DELETE FROM ReviewRecommend r WHERE r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ReviewRecommend r WHERE r.review.id = :reviewId")
    void deleteAllByReviewId(@Param("reviewId") Long reviewId);
}
