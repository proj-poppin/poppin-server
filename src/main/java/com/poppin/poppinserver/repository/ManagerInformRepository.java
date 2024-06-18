package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ManagerInform;
import com.poppin.poppinserver.domain.Popup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerInformRepository extends JpaRepository<ManagerInform, Long> {
    void deleteAllByPopupId(Popup popup);

    Page<ManagerInform> findAllByProgress(Pageable pageable, String progress);
}
