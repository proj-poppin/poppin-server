package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    @Query("SELECT token FROM FCMToken token WHERE token.token = :token")
    FCMToken findByToken(@Param("token") String token);

    @Query("SELECT token FROM FCMToken token WHERE token.token = :token")
    Optional<FCMToken> findByTokenOpt(@Param("token") String token);

    @Query("SELECT DISTINCT t.tokenId FROM PopupTopic t WHERE t.topicCode = :code  AND t.popup.id = :popupId")
    List<FCMToken> findTokenIdByTopicAndType(String code, @Param("popupId") Long popupId);

    @Query("SELECT token FROM FCMToken token WHERE token.exp_dtm <= :now")
    List<FCMToken> findExpiredTokenList(LocalDateTime now);

    @Query("DELETE FROM FCMToken token WHERE token.token = :fcmTokenOptional")
    void delete(Optional<FCMToken> fcmTokenOptional);

    @Query("SELECT token FROM FCMToken token WHERE token.user.id = :userId")
    Optional<FCMToken> findByUserId(Long userId);

    @Query("SELECT token FROM FCMToken token WHERE token.token = :fcmToken")
    Optional<FCMToken> findByFcmToken(@Param("fcmToken") String fcmToken);
}
