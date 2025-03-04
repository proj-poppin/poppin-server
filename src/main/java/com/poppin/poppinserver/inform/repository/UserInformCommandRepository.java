package com.poppin.poppinserver.inform.repository;

import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.domain.UserInform;
import com.poppin.poppinserver.popup.domain.Popup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserInformCommandRepository extends JpaRepository<UserInform, Long> {
    void deleteAllByPopupId(Popup popup);

    @Modifying
    @Query("DELETE FROM UserInform ui WHERE ui.informerId.id = :informerId")
    void deleteAllByInformerId(@Param("informerId") Long informerId);
}
