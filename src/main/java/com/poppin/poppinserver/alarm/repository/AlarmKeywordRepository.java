package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.AlarmKeyword;
import com.poppin.poppinserver.popup.domain.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmKeywordRepository extends JpaRepository<AlarmKeyword, Long> {
    List<AlarmKeyword> findByPopupId(Popup popupId);

    void deleteAllByPopupId(Popup popupId);
}
