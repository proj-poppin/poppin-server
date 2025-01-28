package com.poppin.poppinserver.popup.usecase;

import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@UseCase
public interface PosterImageCommandUseCase {
    List<PosterImage> savePosterList(List<MultipartFile> images, Popup popup);
}
