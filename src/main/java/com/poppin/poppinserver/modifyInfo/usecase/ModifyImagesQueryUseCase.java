package com.poppin.poppinserver.modifyInfo.usecase;

import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;

import java.util.List;

public interface ModifyImagesQueryUseCase {
    List<ModifyImages> findModifyImagesByModifyInfo(ModifyInfo modifyInfo);
}
