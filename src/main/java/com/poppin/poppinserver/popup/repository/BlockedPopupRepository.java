package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.BlockedPopup;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedPopupRepository extends JpaRepository<BlockedPopup, Long> {
    Optional<BlockedPopup> findByPopupIdAndUserId(Popup popupId, User userId);

    @Modifying
    @Query("DELETE FROM BlockedPopup bp WHERE bp.userId.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    void deleteAllByPopupId(Popup popup);

    @Query("SELECT CASE WHEN COUNT(bp) > 0 THEN TRUE ELSE FALSE END " +
            "FROM BlockedPopup bp WHERE bp.popupId.id = :popupId AND bp.userId.id = :userId")
    Boolean existsByPopupIdAndUserId(@Param("popupId") Long popupId, @Param("userId") Long userId);

    List<BlockedPopup> findAllByUserId(User userId);
}
