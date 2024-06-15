package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Interest.InterestId> {
    Optional<Interest> findByUserIdAndPopupId(Long userId, Long popupId);

    void deleteAllByPopupId(Long popupId);
}
