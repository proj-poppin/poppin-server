package com.poppin.poppinserver.interest.repository;

import com.poppin.poppinserver.interest.domain.Interest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Interest.InterestId> {
    Optional<Interest> findByUserIdAndPopupId(Long userId, Long popupId);

    Boolean existsByUserIdAndPopupId(Long userId, Long popupId);

    void deleteAllByPopupId(Long popupId);

    @Modifying
    @Query("DELETE FROM Interest i WHERE i.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Interest i WHERE i.user.id = :userId")
    List<Interest> findByUserId(Long userId);
}
