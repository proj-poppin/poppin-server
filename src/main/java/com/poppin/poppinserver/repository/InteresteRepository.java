package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Intereste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InteresteRepository extends JpaRepository<Intereste, Intereste.InteresteId> {
    Optional<Intereste> findByUserIdAndPopupId(Long userId, Long popupId);
}
