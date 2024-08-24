package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.ReopenDemand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReopenDemandRepository extends JpaRepository<ReopenDemand, Long> {

}
