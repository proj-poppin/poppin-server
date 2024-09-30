package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "notification_token")
public class FCMToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "token", nullable = false, columnDefinition = "TINYTEXT")
    private String token; // 토큰, 최대 255자

    @Column(name = "modify_date", nullable = false)
    private LocalDateTime mod_dtm; // 토큰 갱신일

    @Column(name = "expired_date", nullable = false)
    private LocalDateTime exp_dtm; // 토큰 만료일

    @Builder
    public FCMToken(User user, String token, LocalDateTime mod_dtm) {
        this.user = user;
        this.token = token;
        this.mod_dtm = mod_dtm;
        this.exp_dtm = mod_dtm.plusMonths(1);
    }

    // 토큰 갱신
    public void refreshToken() {
        this.mod_dtm = LocalDateTime.now();
        this.exp_dtm = mod_dtm.plusMonths(1); // 1달 갱신
    }

    public void regenerateToken(FCMToken fcmToken) {
        this.token = fcmToken.getToken();
    }
}
