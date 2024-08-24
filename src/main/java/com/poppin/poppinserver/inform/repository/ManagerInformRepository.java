package com.poppin.poppinserver.inform.repository;

import com.poppin.poppinserver.inform.domain.ManagerInform;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.core.type.EInformProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagerInformRepository extends JpaRepository<ManagerInform, Long> {
    void deleteAllByPopupId(Popup popup);

    Page<ManagerInform> findAllByProgress(Pageable pageable, EInformProgress progress);

    @Modifying
    @Query("DELETE FROM ManagerInform mi WHERE mi.informerId.id = :informerId")
    void deleteAllByInformerId(@Param("informerId") Long informerId);
}
