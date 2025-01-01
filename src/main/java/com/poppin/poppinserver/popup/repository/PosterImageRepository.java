package com.poppin.poppinserver.popup.repository;

import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.popup.domain.PosterImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PosterImageRepository extends JpaRepository<PosterImage, Long> {
    List<PosterImage> findByPopupId(Popup popupId);

    List<PosterImage> findAllByPopupId(Popup popupId);

    void deleteAllByPopupId(Popup popupId);

    @Query("SELECT pi FROM PosterImage pi WHERE pi.popupId.id IN :popupIds")
    List<PosterImage> findAllByPopupIds(@Param("popupIds") List<Long> popupIds);

}
