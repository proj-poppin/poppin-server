package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PosterImage;
import com.poppin.poppinserver.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    @Query("SELECT ri.imageUrl FROM ReviewImage ri WHERE ri.review.id = :reviewId")
    List<String> findUrlAllByReviewId(@Param("reviewId") Long reviewId);

    List<ReviewImage> findAllByPopupId(Popup popupId);
}
