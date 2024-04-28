package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.NotificationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationTopicRepository extends JpaRepository<NotificationTopic, Long> {

    @Query("SELECT NT FROM NotificationTopic NT WHERE NT.topic = :popupName")
    List<NotificationTopic> findByPopupName(@Param("popupName") String popupName);
}
