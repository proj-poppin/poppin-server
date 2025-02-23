package com.poppin.poppinserver.modifyInfo.service;

import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.modifyInfo.repository.ModifyInfoQueryRepository;
import com.poppin.poppinserver.modifyInfo.usecase.ModifyInfoCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModifyInfoCommandService implements ModifyInfoCommandUseCase {
    private final ModifyInfoQueryRepository modifyInfoQueryRepository;

    // service는 다른 UseCase를 의존하면 안댐
    // ModifyInfo가 ModifyImages를 의존하는 것은 자연스러움
    private final ModifyImagesCommandService modifyImagesCommandService;

    @Override
    public void deleteAllModifyInfo(Long userId) {
        List<ModifyInfo> modifyInfos = modifyInfoQueryRepository.findAllByUserId(userId);
        for (ModifyInfo modifyInfo : modifyInfos) {
            modifyImagesCommandService.deleteModifyImageList(modifyInfo);
        }
        modifyInfoQueryRepository.deleteAllByUserId(userId);
    }
}
