package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageReposiroty;
import com.poppin.poppinserver.modifyInfo.usecase.ModifyImagesQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyImagesQueryService implements ModifyImagesQueryUseCase {
    private final ModifyImageReposiroty modifyImageReposiroty;

    @Override
    public List<ModifyImages> findModifyImagesByModifyInfo(ModifyInfo modifyInfo) {
        return modifyImageReposiroty.findByModifyId(modifyInfo);
    }
}
