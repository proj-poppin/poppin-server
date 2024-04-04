package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ModifyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModifyInformRepository extends JpaRepository<ModifyInfo, Long> {
}
