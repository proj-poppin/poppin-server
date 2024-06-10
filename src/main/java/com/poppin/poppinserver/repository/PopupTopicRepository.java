package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.PopupTopic;
import com.poppin.poppinserver.type.EPopupTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PopupTopicRepository extends JpaRepository<PopupTopic, Long> {

    @Query("SELECT PT FROM PopupTopic PT WHERE PT.tokenId = :token AND PT.topic = :topic AND Popup = :popup")
    PopupTopic findByTokenAndTopic(@Param("token") String token , @Param("topic") EPopupTopic topic, @Param("popup") Popup popup);

    @Query("SELECT t.tokenId FROM PopupTopic t WHERE t.topic IN (:topic)  AND t.popup.id = :popupId")
    List<String>findTokenIdByTopicAndType(EPopupTopic topic, @Param("popupId")Long popupId);
}
