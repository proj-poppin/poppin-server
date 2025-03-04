package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PosterImageCommandRepository;
import com.poppin.poppinserver.popup.repository.PosterImageQueryRepository;
import com.poppin.poppinserver.popup.usecase.PosterImageCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PosterImageCommandService implements PosterImageCommandUseCase {
    private final PosterImageQueryRepository posterImageQueryRepository;
    private final PosterImageCommandRepository posterImageCommandRepository;

    private final S3Service s3Service;

    public List<PosterImage> savePosterList(List<MultipartFile> images, Popup popup) {
        List<String> fileUrls = s3Service.uploadPopupPoster(images, popup.getId());

        List<PosterImage> posterImages = new ArrayList<>();
        for (String url : fileUrls) {
            PosterImage posterImage = PosterImage.builder()
                    .posterUrl(url)
                    .popup(popup)
                    .build();
            posterImages.add(posterImage);
        }
        posterImageCommandRepository.saveAll(posterImages);

        return posterImages;
    }

    @Override
    public List<PosterImage> copyPosterList(Popup popup, Popup proxyPopup) {
        List<PosterImage> posterImages = posterImageQueryRepository.findByPopupId(popup);
        List<String> posterUrls = posterImages.stream()
                .map(PosterImage::getPosterUrl)
                .toList();

        List<String> proxyUrls = s3Service.copyImageListToAnotherFolder(posterUrls, proxyPopup.getId());

        List<PosterImage> proxyImages = new ArrayList<>();
        for (String proxyUrl : proxyUrls) {
            PosterImage proxyImage = PosterImage.builder()
                    .posterUrl(proxyUrl)
                    .popup(proxyPopup)
                    .build();
            proxyImages.add(proxyImage);
        }
        posterImageCommandRepository.saveAll(proxyImages);

        return posterImages;
    }

    @Override
    public void deletePosterList(Popup popup) {
        List<PosterImage> posterImages = posterImageQueryRepository.findAllByPopupId(popup);
        List<String> fileUrls = posterImages.stream()
                .map(PosterImage::getPosterUrl)
                .toList();
        if (!fileUrls.isEmpty()) {
            s3Service.deleteMultipleImages(fileUrls);
            posterImageCommandRepository.deleteAllByPopupId(popup);
        }
    }
}
