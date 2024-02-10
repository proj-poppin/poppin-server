package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupRepository extends JpaRepository<Popup, Long> {
}
