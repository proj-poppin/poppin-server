package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId")
    void deleteAllByUserId(Long userId);
}
