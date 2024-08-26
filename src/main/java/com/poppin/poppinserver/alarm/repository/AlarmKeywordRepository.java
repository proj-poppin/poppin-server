package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.PopupAlarmKeyword;
import com.poppin.poppinserver.popup.domain.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmKeywordRepository extends JpaRepository<PopupAlarmKeyword, Long> {
    List<PopupAlarmKeyword> findByPopupId(Popup popupId);

    void deleteAllByPopupId(Popup popupId);
}
