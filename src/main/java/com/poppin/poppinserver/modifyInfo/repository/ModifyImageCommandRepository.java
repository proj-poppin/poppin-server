package com.poppin.poppinserver.modifyInfo.repository;

import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModifyImageCommandRepository extends JpaRepository<ModifyImages, Long> {
    void deleteAllByModifyId(ModifyInfo modifyId);

    @Modifying
    @Query("DELETE FROM ModifyImages mi WHERE mi.modifyId.id = :modifyId")
    void deleteAllByModifyId(@Param("modifyId") Long modifyId);
}
