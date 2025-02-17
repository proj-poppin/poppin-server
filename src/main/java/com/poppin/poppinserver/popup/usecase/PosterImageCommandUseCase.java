package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@UseCase
public interface PosterImageCommandUseCase {
    // 팝업 이미지 생성
    List<PosterImage> savePosterList(List<MultipartFile> images, Popup popup);

    // 팝업 이미지 삭제
    void deletePosterList(Popup popup);
}
