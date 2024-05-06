package com.poppin.poppinserver.repository;

import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.type.ELoginProvider;
import com.poppin.poppinserver.type.EUserRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // 회원 탈퇴한 유저인지 확인하는 쿼리 추가
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false AND u.deletedAt IS NULL")
    Optional<User> findById(Long id);
    Optional<User> findByNickname(String nickname);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.refreshToken = :refreshToken, u.isLogin = :loginStatus WHERE u.id = :id")
    void updateRefreshTokenAndLoginStatus(Long id, String refreshToken, boolean loginStatus);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.provider = :eLoginProvider")
    Optional<User> findByIdAndELoginProvider(Long id, ELoginProvider eLoginProvider);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.role = :role")
    Optional<User> findByEmailAndRole(String email, EUserRole role);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isLogin = :isLogin AND u.refreshToken IS NOT NULL")
    Optional<User> findByIdAndIsLoginAndRefreshTokenNotNull(Long id, boolean isLogin);

    List<User> findAllByDeletedAtIsNotNull();

    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname% OR u.email LIKE %:email% ORDER BY u.nickname ASC")
    List<User> findByNicknameContainingOrEmailContainingOrderByNickname(String nickname, String email);
}
