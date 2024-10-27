package com.poppin.poppinserver.user.repository;

import com.poppin.poppinserver.user.domain.BlockedUser;
import com.poppin.poppinserver.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    @Query("SELECT bu.blockedUserId.id FROM BlockedUser bu WHERE bu.userId.id = :userId")
    List<Long> findBlockedUserIdsByUserId(Long userId);

    @Query("SELECT bu.id FROM BlockedUser bu WHERE bu.userId.id = :userId AND bu.blockedUserId.id = :blockedUserId")
    Optional<BlockedUser> findByUserIdAndBlockedUserId(Long userId, Long blockedUserId);

    @Modifying
    @Query("DELETE FROM BlockedUser bu WHERE bu.userId.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM BlockedUser bu WHERE bu.blockedUserId.id = :userId")
    void deleteAllByBlockedId(@Param("userId") Long userId);

    List<BlockedUser> findAllByUserId(User UserId);
}
