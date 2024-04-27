package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.NotificationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTopicRepository extends JpaRepository<NotificationTopic, Long> {
}
