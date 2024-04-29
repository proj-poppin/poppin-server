package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.NotificationTopic;
import com.poppin.poppinserver.type.ETopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface NotificationTopicRepository extends JpaRepository<NotificationTopic, Long> {

    @Query("SELECT NT FROM NotificationTopic NT WHERE NT.tokenId = :token AND NT.topic = :topic")
    NotificationTopic findByTokenAndTopic(@Param("token") String token , @Param("topic") ETopic topic);
}
