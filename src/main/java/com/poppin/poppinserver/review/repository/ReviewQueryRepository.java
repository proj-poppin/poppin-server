package com.poppin.poppinserver.review.repository;

import com.poppin.poppinserver.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewQueryRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.id = :reviewId")
    Optional<Review> findByReviewId(Long reviewId);

    @Query("SELECT r FROM Review r where r.user.id = :userId order by r.createdAt asc ")
    List<Review> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Review r JOIN FETCH r.popup WHERE r.user.id = :userId")
    List<Review> findByUserIdWithPopup(@Param("userId") Long userId);

    List<Review> findByPopupId(Long popupId);

    @Query("SELECT r FROM Review r where r.user.id = :userId and r.popup.id = :popupId order by r.createdAt asc ")
    Optional<Review> findByUserIdAndPopupId(@Param("userId")Long userId , @Param("popupId") Long popupId);

    @Query("SELECT r FROM Review r WHERE r.popup.id = :popupId AND r.isVisible = true ORDER BY r.recommendCnt DESC")
    List<Review> findAllByPopupIdOrderByRecommendCntDesc(@Param("popupId") Long popupId);

    @Query("SELECT r FROM Review r join Popup p on p.id = r.popup.id WHERE p.id = :popupId AND r.id = :reviewId")
    Review findByReviewIdAndPopupId(@Param("reviewId") Long reviewId, @Param("popupId") Long popupId);

    @Query("SELECT r FROM Review r WHERE r.id = :reviewId AND r.popup.id = :popupId ")
    Review findByIdAndPopupId(@Param("reviewId") Long reviewId, @Param("popupId") Long popupId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.isVisible = false")
    Long countByUserIdAndIsVisibleFalse(Long userId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.isVisible = :hidden ORDER BY r.createdAt DESC")
    Page<Review> findByUserIdAndIsVisibleOrderByCreatedAtDesc(Long userId, Pageable pageable,
                                                              @Param("hidden") Boolean hidden);

    @Query("SELECT p.id FROM Popup p JOIN Visit v on p.id = v.popup.id WHERE  v.user.id = :userId ")
    List<Long> findVisitedPopupByUserId(@Param("userId") Long userId);
}
