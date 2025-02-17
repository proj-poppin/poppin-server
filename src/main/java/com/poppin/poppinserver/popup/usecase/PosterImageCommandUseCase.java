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

    // 기존 팝업으로부터 프록시 팝업 이미지 생성
    List<PosterImage> copyPosterList(Popup popup, Popup proxyPopup);

    // 팝업 이미지 삭제
    void deletePosterList(Popup popup);
}
