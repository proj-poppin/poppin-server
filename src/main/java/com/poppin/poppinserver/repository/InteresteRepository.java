package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Intereste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteresteRepository extends JpaRepository<Intereste, Long> {
}
