package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.BlockedPopup;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockedPopupCommandRepository extends JpaRepository<BlockedPopup, Long> {
    @Modifying
    @Query("DELETE FROM BlockedPopup bp WHERE bp.userId.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    void deleteAllByPopupId(Popup popup);
}
