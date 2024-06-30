package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    @Query("SELECT nt FROM NotificationToken nt WHERE nt.token = :token")
    NotificationToken findByToken(@Param("token") String token);

    @Query("SELECT DISTINCT t.tokenId FROM PopupTopic t WHERE t.topicCode = :code  AND t.popup.id = :popupId")
    List<NotificationToken> findTokenIdByTopicAndType(String code, @Param("popupId")Long popupId);
}
