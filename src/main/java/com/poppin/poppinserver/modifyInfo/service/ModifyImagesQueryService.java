package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.repository.ModifyImageQueryRepository;
import com.poppin.poppinserver.modifyInfo.usecase.ModifyImagesQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyImagesQueryService implements ModifyImagesQueryUseCase {
    private final ModifyImageQueryRepository ModifyImageQueryRepository;

    @Override
    public List<ModifyImages> findModifyImagesAll(ModifyInfo modifyInfo) {
        return ModifyImageQueryRepository.findByModifyId(modifyInfo);
    }
}
