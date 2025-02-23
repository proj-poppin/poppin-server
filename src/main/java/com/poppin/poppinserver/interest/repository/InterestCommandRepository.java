package com.poppin.poppinserver.interest.repository;

import com.poppin.poppinserver.interest.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterestCommandRepository extends JpaRepository<Interest, Interest.InterestId> {
    void deleteAllByPopupId(Long popupId);

    @Modifying
    @Query("DELETE FROM Interest i WHERE i.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
