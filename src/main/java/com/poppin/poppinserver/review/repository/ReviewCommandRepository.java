package com.poppin.poppinserver.review.repository;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCommandRepository extends JpaRepository<Review, Long> {

    @Modifying
    @Query("DELETE FROM Review r WHERE r.user.id = :userId")
    void deleteAllByUserId(Long userId);

    void deleteAllByPopup(Popup popup);

}
