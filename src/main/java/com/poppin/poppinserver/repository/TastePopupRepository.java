package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.TastePopup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TastePopupRepository extends JpaRepository<TastePopup, Long> {
    Optional<TastePopup> findById(Long id);
}
