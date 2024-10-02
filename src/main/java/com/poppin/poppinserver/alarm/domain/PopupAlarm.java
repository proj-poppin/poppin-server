package com.poppin.poppinserver.alarm.domain;

import com.poppin.poppinserver.popup.domain.Popup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "popup_alarm")
public class PopupAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popupId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "icon", nullable = false)
    private String icon; // icon

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead; // 읽음 여부

    @Builder
    public PopupAlarm(Popup popupId, String token, String title, String body, String icon,
                      LocalDate createdAt, Boolean isRead) {
        this.popupId = popupId;
        this.token = token;
        this.title = title;
        this.body = body;
        this.icon = icon;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // isRead를 변경하는 메서드 추가
    public void markAsRead() {
        this.isRead = true;
    }
}
