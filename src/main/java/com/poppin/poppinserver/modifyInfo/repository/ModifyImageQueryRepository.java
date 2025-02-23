package com.poppin.poppinserver.modifyInfo.repository;

import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModifyImageQueryRepository extends JpaRepository<ModifyImages, Long> {
    List<ModifyImages> findByModifyId(ModifyInfo modifyId);
}
