package com.poppin.poppinserver.inform.repository;

import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModifyInformRepository extends JpaRepository<ModifyInfo, Long> {
    @Query("select m from ModifyInfo m where m.isExecuted = :isExecuted")
    Page<ModifyInfo> findAllByIsExecuted(Pageable pageable, @Param("isExecuted") Boolean isExecuted);

    List<ModifyInfo> findAllByOriginPopupId(Long originPopupId);

    @Modifying
    @Query("DELETE FROM ModifyInfo mi WHERE mi.userId.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Query("select m from ModifyInfo m where m.userId.id = :userId")
    List<ModifyInfo> findAllByUserId(Long userId);
}
