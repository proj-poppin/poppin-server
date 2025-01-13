package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long> {

}
