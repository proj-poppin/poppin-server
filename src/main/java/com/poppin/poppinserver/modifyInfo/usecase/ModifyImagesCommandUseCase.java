package com.poppin.poppinserver.modifyInfo.usecase;

import com.poppin.poppinserver.modifyInfo.domain.ModifyImages;
import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ModifyImagesCommandUseCase {
    // 팝업 이미지 생성
    List<String> saveModifyImagerList(List<MultipartFile> images, ModifyInfo modifyInfo);

    // 팝업 이미지 삭제
    void deleteModifyImageList(ModifyInfo modifyInfo);
}
