package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ManagerInform;
import com.poppin.poppinserver.domain.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerInformRepository extends JpaRepository<ManagerInform, Long> {
    void deleteAllByPopupId(Popup popup);
}
