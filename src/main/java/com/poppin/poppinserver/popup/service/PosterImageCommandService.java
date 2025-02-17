package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import com.poppin.poppinserver.popup.repository.PosterImageRepository;
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
    private final PosterImageRepository posterImageRepository;

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
        posterImageRepository.saveAll(posterImages);

        return posterImages;
    }

    @Override
    public void deletePosterList(Popup popup) {
        List<PosterImage> posterImages = posterImageRepository.findAllByPopupId(popup);
        List<String> fileUrls = posterImages.stream()
                .map(PosterImage::getPosterUrl)
                .toList();
        if (!fileUrls.isEmpty()) {
            s3Service.deleteMultipleImages(fileUrls);
            posterImageRepository.deleteAllByPopupId(popup);
        }
    }
}
