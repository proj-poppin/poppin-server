package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.BlockedPopup;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockedPopupRepository extends JpaRepository<BlockedPopup, Long> {
    Optional<BlockedPopup> findByPopupIdAndUserId(Popup popupId, User userId);
}
