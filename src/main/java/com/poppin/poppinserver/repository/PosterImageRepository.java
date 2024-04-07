package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ModifyImages;
import com.poppin.poppinserver.domain.ModifyInfo;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PosterImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosterImageRepository extends JpaRepository<PosterImage, Long> {
    List<PosterImage> findByPopupId(Popup popupId);
}
