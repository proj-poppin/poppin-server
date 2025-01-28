package com.poppin.poppinserver.user.repository;

import com.poppin.poppinserver.user.domain.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedUserCommandRepository extends JpaRepository<BlockedUser, Long> {
    @Modifying
    @Query("DELETE FROM BlockedUser bu WHERE bu.userId.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM BlockedUser bu WHERE bu.blockedUserId.id = :userId")
    void deleteAllByBlockedId(@Param("userId") Long userId);
}
