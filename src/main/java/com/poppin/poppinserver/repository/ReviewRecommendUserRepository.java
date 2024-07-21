package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.Review;
import com.poppin.poppinserver.domain.ReviewRecommendUser;
import com.poppin.poppinserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRecommendUserRepository extends JpaRepository<ReviewRecommendUser, Long> {

    Optional<ReviewRecommendUser> findByUserAndReview(User user , Review review);

    void deleteAllByReviewPopup(Popup popup);

    @Modifying
    @Query("DELETE FROM ReviewRecommendUser r WHERE r.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ReviewRecommendUser r WHERE r.review.id = :reviewId")
    void deleteAllByReviewId(@Param("reviewId") Long reviewId);
}
