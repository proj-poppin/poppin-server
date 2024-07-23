package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.BlockedPopup;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedPopupRepository extends JpaRepository<BlockedPopup, Long> {
    Optional<BlockedPopup> findByPopupIdAndUserId(Popup popupId, User userId);

    @Modifying
    @Query("DELETE FROM BlockedPopup bp WHERE bp.userId.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    void deleteAllByPopupId(Popup popup);
}
