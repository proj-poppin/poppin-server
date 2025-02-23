package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.PreferedPopup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreferedPopupCommandRepository extends JpaRepository<PreferedPopup, Long> {
}
