package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ModifyImages;
import com.poppin.poppinserver.domain.ModifyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModifyImageReposiroty extends JpaRepository<ModifyImages, Long> {
    List<ModifyImages> findByModifyId(ModifyInfo modifyId);

    void deleteAllByModifyId(ModifyInfo modifyId);

    @Query("DELETE FROM ModifyImages mi WHERE mi.modifyId.id = :modifyId")
    void deleteAllByModifyId(Long modifyId);
}
