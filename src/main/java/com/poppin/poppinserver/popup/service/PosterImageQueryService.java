package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PosterImageQueryRepository;
import com.poppin.poppinserver.popup.usecase.PosterImageQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PosterImageQueryService implements PosterImageQueryUseCase {
    private final PosterImageQueryRepository posterImageQueryRepository;

    @Override
    public List<PosterImage> findAllPosterImageByPopupIds(List<Long> popupIds) {
        return posterImageQueryRepository.findAllByPopupIds(popupIds);
    }
}
