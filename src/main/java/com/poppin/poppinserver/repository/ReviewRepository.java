package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.popup.id = :popupId ORDER BY r.recommendCnt DESC")
    List<Review> findAllByPopupIdOrderByRecommendCntDesc(@Param("popupId") Long popupId, PageRequest pageable);

    @Query("SELECT r FROM Review r join Popup p on p.id = r.popup.id WHERE p.id = :popupId AND r.id = :reviewId")
    Review findByReviewIdAndPopupId(@Param("reviewId") Long reviewId, @Param("popupId") Long popupId);

    @Query("SELECT r FROM Review r where r.user.id = :userId order by r.createdAt asc ")
    List<Review> findByUserId(@Param("userId")Long userId);

    @Query("SELECT r FROM Review r WHERE r.id = :reviewId AND r.popup.id = :popupId ")
    Review findByIdAndPopupId(@Param("reviewId") Long reviewId, @Param("popupId") Long popupId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.isVisible = false")
    Long countByUserIdAndIsVisibleFalse(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteAllByUserId(Long userId);

    void deleteAllByPopup(Popup popup);
}
