package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.ManagerInform;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.type.EInformProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManagerInformRepository extends JpaRepository<ManagerInform, Long> {
    void deleteAllByPopupId(Popup popup);

    Page<ManagerInform> findAllByProgress(Pageable pageable, EInformProgress progress);

    @Query("DELETE FROM ManagerInform mi WHERE mi.informerId.id = :informerId AND mi.progress = 'NOTEXECUTED'")
    void deleteAllByInformerIdAndProgress(Long userId);
}
