package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    @Query("SELECT bu.blockedUserId.id FROM BlockedUser bu WHERE bu.userId.id = :userId")
    List<Long> findBlockedUserIdsByUserId(Long userId);
}
