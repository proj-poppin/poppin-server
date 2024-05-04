package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ReopenDemandUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReopenDemandUserRepository extends JpaRepository<ReopenDemandUser, Long> {

}
