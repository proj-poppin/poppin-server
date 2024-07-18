package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.UserInform;
import com.poppin.poppinserver.type.EInformProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserInformRepository extends JpaRepository<UserInform, Long> {
    void deleteAllByPopupId(Popup popup);

    Page<UserInform> findAllByProgress(Pageable pageable, EInformProgress pregress);

    @Query("DELETE FROM UserInform ui WHERE ui.informerId.id = :informerId AND ui.progress = 'NOTEXECUTED'")
    void deleteAllByInformerIdAndProgress(Long informerId);
}
