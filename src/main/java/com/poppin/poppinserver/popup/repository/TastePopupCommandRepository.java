package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.TastePopup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TastePopupCommandRepository extends JpaRepository<TastePopup, Long> {
}
