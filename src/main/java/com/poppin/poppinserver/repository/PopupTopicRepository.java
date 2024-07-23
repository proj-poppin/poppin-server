package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.FCMToken;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PopupTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PopupTopicRepository extends JpaRepository<PopupTopic, Long> {

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.tokenId = :token AND PT.topicCode = :code AND PT.popup = :popupId")
    PopupTopic findByTokenAndTopic(FCMToken token, @Param("code") String code, @Param("popupId") Popup popupId);

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.tokenId = :token")
    Optional<List<PopupTopic>> findByToken(FCMToken token);

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.popup = :popup")
    List<PopupTopic> findByPopup(Popup popup);

    void deleteAllByPopup(Popup popup);
}
