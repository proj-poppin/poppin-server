package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.type.ELoginProvider;
import com.poppin.poppinserver.type.EUserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByNickname(String nickname);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.refreshToken = :refreshToken, u.isLogin = :loginStatus WHERE u.email = :email")
    void updateRefreshTokenAndLoginStatus(String email, String refreshToken, boolean loginStatus);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.provider = :eLoginProvider")
    Optional<User> findByEmailAndELoginProvider(String email, ELoginProvider eLoginProvider);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.role = :role")
    Optional<User> findByEmailAndRole(String email, EUserRole role);
}
