package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.AlarmKeyword;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PosterImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmKeywordRepository extends JpaRepository<AlarmKeyword, Long> {
    List<AlarmKeyword> findByPopupId(Popup popupId);

    void deleteAllByPopupId(Popup popupId);
}
