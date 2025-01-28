package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.PopupTopic;
import com.poppin.poppinserver.popup.domain.Popup;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PopupTopicRepository extends JpaRepository<PopupTopic, Long> {

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.user = :user AND PT.topicCode = :code AND PT.popup = :popupId")
    PopupTopic findPopupTopicByTopicCode(User user, @Param("code") String code, @Param("popupId") Popup popupId);

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.user = :user")
    Optional<List<PopupTopic>> findByUser(User user);

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.popup = :popup")
    List<PopupTopic> findByPopup(Popup popup);

    void deleteAllByPopup(Popup popup);
}
