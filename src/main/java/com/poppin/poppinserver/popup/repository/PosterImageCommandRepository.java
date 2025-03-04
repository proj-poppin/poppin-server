package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PosterImageCommandRepository extends JpaRepository<PosterImage, Long> {
    void deleteAllByPopupId(Popup popupId);
}
