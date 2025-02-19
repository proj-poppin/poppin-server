package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import com.poppin.poppinserver.popup.usecase.PosterImageCommandUseCase;
import com.poppin.poppinserver.popup.usecase.PosterImageQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PosterImageQueryService implements PosterImageQueryUseCase {
    private final PosterImageRepository posterImageRepository;

    @Override
    public List<PosterImage> findAllPosterImageByPopupIds(List<Long> popupIds) {
        return posterImageRepository.findAllByPopupIds(popupIds);
    }
}
