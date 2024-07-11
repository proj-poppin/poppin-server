package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "blocked_users")
public class BlockedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userId;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", referencedColumnName = "id", nullable = false)
    private User blockedUserId;

    @Builder
    public BlockedUser(User userId, User blockedUserId) {
        this.userId = userId;
        this.blockedUserId = blockedUserId;
    }
}
