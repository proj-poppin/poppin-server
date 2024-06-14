package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ModifyInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModifyInformRepository extends JpaRepository<ModifyInfo, Long> {
    @Query("select m from ModifyInfo m where m.isExecuted = :isExecuted")
    Page<ModifyInfo> findAllByIsExecuted(Pageable pageable, @Param("isExecuted") Boolean isExecuted);
}
