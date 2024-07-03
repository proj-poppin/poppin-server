package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    @Query("SELECT nt FROM FCMToken nt WHERE nt.token = :token")
    FCMToken findByToken(@Param("token") String token);

    @Query("SELECT DISTINCT t.tokenId FROM PopupTopic t WHERE t.topicCode = :code  AND t.popup.id = :popupId")
    List<FCMToken> findTokenIdByTopicAndType(String code, @Param("popupId")Long popupId);

    @Query("SELECT token FROM FCMToken token WHERE token.exp_dtm <= :now")
    List<FCMToken> findExpiredTokenList(LocalDateTime now);
}
