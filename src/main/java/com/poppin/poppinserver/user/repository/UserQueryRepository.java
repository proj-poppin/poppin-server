package com.poppin.poppinserver.user.repository;

import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.domain.type.ELoginProvider;
import com.poppin.poppinserver.user.domain.type.EUserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQueryRepository extends JpaRepository<User, Long> {
    // 회원 탈퇴하지 않은 유저 전용 쿼리
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmail(String email);

    // 회원 탈퇴하지 않은 유저 전용 쿼리
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false AND u.deletedAt IS NULL")
    Optional<User> findById(Long id);

    // 회원 탈퇴하지 않은 유저 전용 쿼리
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.role = :role AND u.isDeleted = false")
    User findByEmailAndRole(String email, EUserRole role);

    // 탈퇴 유저 포함 검색
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByUserId(Long id);

    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.provider = :eLoginProvider")
    Optional<User> findByIdAndELoginProvider(Long id, ELoginProvider eLoginProvider);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.provider = :eLoginProvider")
    Optional<User> findByEmailAndProvider(String email, ELoginProvider eLoginProvider);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.refreshToken IS NOT NULL")
    Optional<User> findByIdAndRefreshTokenNotNull(Long id);

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL AND u.isDeleted = true")
    List<User> findAllByDeletedAtIsNotNullAndIsDeleted();

    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:nickname% OR u.email LIKE %:email% ORDER BY u.nickname ASC")
    List<User> findByNicknameContainingOrEmailContainingOrderByNickname(String nickname, String email);

    @Query("SELECT u FROM User u ORDER BY u.nickname ASC")
    Page<User> findAllByOrderByNicknameAsc(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.requiresSpecialCare = :requiresSpecialCare ORDER BY u.nickname ASC")
    Page<User> findByRequiresSpecialCareOrderByNicknameAsc(boolean requiresSpecialCare, Pageable pageable);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);
}
