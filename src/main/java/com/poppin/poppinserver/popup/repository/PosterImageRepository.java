package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosterImageRepository extends JpaRepository<PosterImage, Long> {
    List<PosterImage> findByPopupId(Popup popupId);

    List<PosterImage> findAllByPopupId(Popup popupId);

    void deleteAllByPopupId(Popup popupId);
}
