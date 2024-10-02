package com.poppin.poppinserver.alarm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "information_is_read")
public class InformIsRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inform_alarm", referencedColumnName = "id", nullable = false)
    private InformAlarm informAlarm;

    @ManyToOne
    @JoinColumn(name = "fcm_token", referencedColumnName = "id", nullable = false)
    private FCMToken fcmToken;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Builder
    public InformIsRead(InformAlarm informAlarm, FCMToken token) {
        this.informAlarm = informAlarm;
        this.fcmToken = token;
        this.isRead = false;
    }

    // isRead를 변경하는 메서드 추가
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}
