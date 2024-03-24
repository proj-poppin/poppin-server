package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.WhoWithPopup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WhoWithPopupRepository extends JpaRepository<WhoWithPopup, Long> {
    Optional<WhoWithPopup> findById(Long id);
}
