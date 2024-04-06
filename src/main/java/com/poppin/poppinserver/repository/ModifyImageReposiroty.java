package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ModifyImages;
import com.poppin.poppinserver.domain.ModifyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModifyImageReposiroty extends JpaRepository<ModifyImages, Long> {
    List<ModifyImages> findByModifyId(ModifyInfo modifyId);
}
