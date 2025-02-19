package com.poppin.poppinserver.popup.usecase;


import com.poppin.poppinserver.core.annotation.UseCase;
import com.poppin.poppinserver.popup.domain.PosterImage;

import java.util.List;

@UseCase
public interface PosterImageQueryUseCase {
    List<PosterImage> findAllPosterImageByPopupIds(List<Long> popupIds);
}
