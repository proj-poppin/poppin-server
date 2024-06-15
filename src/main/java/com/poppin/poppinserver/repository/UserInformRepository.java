package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.UserInform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInformRepository extends JpaRepository<UserInform, Long> {
    void deleteAllByPopupId(Popup popup);
}
