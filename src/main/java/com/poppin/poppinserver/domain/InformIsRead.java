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

    @Builder
    public InformIsRead(InformAlarm informAlarm, FCMToken token) {
        this.informAlarm = informAlarm;
        this.fcmToken = token;
        this.isRead = false;
    }

    // isRead를 변경하는 메서드 추가
    public void markAsRead() {
        this.isRead = true;
    }
}
