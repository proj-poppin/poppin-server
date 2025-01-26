package com.poppin.poppinserver.alarm.repository;

import com.poppin.poppinserver.alarm.domain.FCMToken;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    @Query("SELECT token FROM FCMToken token WHERE token.token = :token")
    FCMToken findByToken(@Param("token") String token);

    @Query("SELECT token FROM FCMToken token WHERE token.token = :token")
    Optional<FCMToken> findByTokenOpt(@Param("token") String token);

    // 1. 원준 계정으로 로그인 -> FCMToken 레코드 생성
    // 2. 관심 팝업 누름 -> PopupTopic테이블 레코드 생성
    // 3. 원준 계정 로그아웃 - > FCMToken테이블 token = null
    // 4. findTokenIdByTopicAndType 메서드 스케줄러 호출 -> FCMToken 레코드 모음 가져옴
    // 5.
    @Query("SELECT DISTINCT t.tokenId FROM PopupTopic t WHERE t.topicCode = :code  AND t.popup.id = :popupId")
    List<FCMToken> findTokenIdByTopicAndType(String code, @Param("popupId") Long popupId);

    @Query("SELECT token FROM FCMToken token WHERE token.exp_dtm <= :now")
    List<FCMToken> findExpiredTokenList(LocalDateTime now);

    @Query("SELECT token FROM FCMToken token WHERE token.user.id = :userId")
    Optional<FCMToken> findByUserId(Long userId);

    FCMToken findByUser(User user);

    @Query("SELECT token.user FROM FCMToken token WHERE token = :fcmToken")
    User findUserByToken(FCMToken fcmToken);
}
