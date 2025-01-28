package com.poppin.poppinserver.user.repository;

import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserCommandRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.id = :id")
    void updateRefreshToken(Long id, String refreshToken);
}
